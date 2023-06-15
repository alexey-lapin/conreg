package com.github.alexeylapin.conreg.http;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.json.Json;
import com.gihtub.alexeylapin.conreg.model.Reference;
import lombok.SneakyThrows;

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
            String uri = reference.getEndpoint() + "/v2/" + reference.getName() + "/manifests/" + reference.getTag();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Accept", "application/vnd.docker.distribution.manifest.v2+json")
                    .GET();
            String body = withAuth(requestBuilder);
            return json.parse(body, ManifestDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private String withAuth(HttpRequest.Builder requestBuilder) {
        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 401 || response.statusCode() == 403) {
            response.headers().firstValue("www-authenticate").ifPresent(value -> {
                String authorization = authenticator.authenticate(value);
                requestBuilder.header("authorization", authorization);
            });
            response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        }
        return response.body();
    }

}
