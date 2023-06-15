package com.github.alexeylapin.conreg.http;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.dto.DockerAuthDto;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.json.jackson.DockerAuthMixin;
import com.gihtub.alexeylapin.conreg.json.jackson.JacksonJson;
import com.gihtub.alexeylapin.conreg.model.Reference;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

class JdkApiClientTest {

    @Test
    void name1() {
        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addMixIn(DockerAuthDto.class, DockerAuthMixin.class);
        JacksonJson jacksonJson = new JacksonJson(objectMapper);
        ApiClient apiClient = new JdkApiClient(httpClient, new Authenticator(httpClient, jacksonJson), jacksonJson);
        ManifestDto manifest = apiClient.getManifest(new Reference("https://registry-1.docker.io",
                "library/alpine", "latest", null));
        System.out.println(manifest);
    }

    @Test
    void name2() {
        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addMixIn(DockerAuthDto.class, DockerAuthMixin.class);
        JacksonJson jacksonJson = new JacksonJson(objectMapper);
        ApiClient apiClient = new JdkApiClient(httpClient, new Authenticator(httpClient, jacksonJson), jacksonJson);
        ManifestDto manifest = apiClient.getManifest(new Reference("https://ghcr.io",
                "alexey-lapin/micronaut-proxy", "latest", null));
        System.out.println(manifest);
    }

}