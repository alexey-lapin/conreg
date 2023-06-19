package com.github.alexeylapin.conreg.http;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.json.Json;
import com.gihtub.alexeylapin.conreg.image.Reference;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JdkApiClient implements ApiClient {

    private final HttpClient httpClient;
    private final Authenticator authenticator;
    private final Json json;

    public JdkApiClient(HttpClient httpClient, Authenticator authenticator, Json json) {
        this.httpClient = httpClient;
        this.authenticator = authenticator;
        this.json = json;
    }

    @Override
    public ManifestDto getManifest(Reference reference) {
        try {
            String uri = String.format(MANIFEST, reference.getEndpoint(), reference.getName(), reference.getTag());
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Accept", "application/vnd.docker.distribution.manifest.v2+json")
                    .GET();
            HttpResponse<String> response = withAuth(requestBuilder, HttpResponse.BodyHandlers.ofString());
            return json.parse(response.body(), ManifestDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getBlob(Reference reference, String digest) {
        try {
            String uri = String.format(BLOB, reference.getEndpoint(), reference.getName(), digest);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .GET();
            HttpResponse<InputStream> response = withAuth(requestBuilder, HttpResponse.BodyHandlers.ofInputStream());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private <T> HttpResponse<T> withAuth(HttpRequest.Builder requestBuilder, HttpResponse.BodyHandler<T> bodyHandler) {
        HttpResponse<T> response = httpClient.send(requestBuilder.build(), bodyHandler);
        if (response.statusCode() == 401 || response.statusCode() == 403) {
            response.headers().firstValue("www-authenticate").ifPresent(value -> {
                String authorization = authenticator.authenticate(value);
                requestBuilder.header("authorization", authorization);
            });
            response = httpClient.send(requestBuilder.build(), bodyHandler);
        }
        return response;
    }

}
