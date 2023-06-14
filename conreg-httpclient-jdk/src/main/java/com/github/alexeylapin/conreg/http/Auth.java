package com.github.alexeylapin.conreg.http;

import com.gihtub.alexeylapin.conreg.client.http.dto.DockerAuthDto;
import com.gihtub.alexeylapin.conreg.json.Json;
import com.gihtub.alexeylapin.conreg.model.Reference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Auth {

    private final HttpClient httpClient;
    private final Json json;

    public Auth(HttpClient httpClient, Json json) {
        this.httpClient = httpClient;
        this.json = json;
    }

    public String get(Reference reference) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://auth.docker.io/token?service=registry.docker.io&scope=repository:" + reference.getName() + ":pull"))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            DockerAuthDto dockerAuthDto = json.parse(response.body(), DockerAuthDto.class);
            return "Bearer " + dockerAuthDto.getToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
