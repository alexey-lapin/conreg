package com.github.alexeylapin.conreg.http;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihtub.alexeylapin.conreg.DefaultRegistryClient;
import com.gihtub.alexeylapin.conreg.RegistryClient;
import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.dto.DockerAuthDto;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.io.DefaultFileOperations;
import com.gihtub.alexeylapin.conreg.io.FileOperations;
import com.gihtub.alexeylapin.conreg.json.jackson.DockerAuthMixin;
import com.gihtub.alexeylapin.conreg.json.jackson.JacksonJson;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.registry.DefaultRegistryOperations;
import com.gihtub.alexeylapin.conreg.registry.RegistryOperations;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.nio.file.Paths;

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

    @Test
    void name3() {
        ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .addMixIn(DockerAuthDto.class, DockerAuthMixin.class);
        JacksonJson jacksonJson = new JacksonJson(objectMapper);

        HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        ApiClient apiClient = new JdkApiClient(httpClient, new Authenticator(httpClient, jacksonJson), jacksonJson);

        RegistryOperations registryOperations = new DefaultRegistryOperations(apiClient);
        FileOperations fileOperations = new DefaultFileOperations(jacksonJson);

        RegistryClient registryClient = new DefaultRegistryClient(registryOperations, fileOperations);

        registryClient.pull(new Reference("https://ghcr.io",
                "alexey-lapin/micronaut-proxy", "0.0.5", null), Paths.get("mic-prox.tar"));
    }

}