package com.github.alexeylapin.conreg.http;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.json.Json;
import com.gihtub.alexeylapin.conreg.model.Reference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JdkApiClient implements ApiClient {

    private final HttpClient httpClient;
    private final Auth auth;

    public JdkApiClient(HttpClient httpClient, Auth auth, Json json) {
        this.httpClient = httpClient;
        this.auth = auth;
    }

    @Override
    public ManifestDto getManifest(Reference reference) {
        try {
            String uri = reference.getEndpoint() + "/v2/" + reference.getName() + "/manifests/" + reference.getTag();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Accept", "application/vnd.docker.distribution.manifest.v2+json")
                    .header("Authorization", auth.get(reference))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
