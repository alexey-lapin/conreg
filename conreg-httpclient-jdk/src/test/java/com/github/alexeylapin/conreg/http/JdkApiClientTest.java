package com.github.alexeylapin.conreg.http;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihtub.alexeylapin.conreg.DefaultRegistryClient;
import com.gihtub.alexeylapin.conreg.RegistryClient;
import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.WellKnownRegistries;
import com.gihtub.alexeylapin.conreg.client.http.dto.DockerAuthDto;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.io.DefaultFileOperations;
import com.gihtub.alexeylapin.conreg.io.FileOperations;
import com.gihtub.alexeylapin.conreg.json.Json;
import com.gihtub.alexeylapin.conreg.json.jackson.DockerAuthMixin;
import com.gihtub.alexeylapin.conreg.json.jackson.JacksonJson;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.registry.DefaultRegistryOperations;
import com.gihtub.alexeylapin.conreg.registry.RegistryOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.nio.file.Paths;

class JdkApiClientTest {

    private RegistryResolver registryResolver;
    private HttpClient httpClient;
    private Json json;

    @BeforeEach
    void setUp() {
        registryResolver = new WellKnownRegistries();

        httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

        ObjectMapper objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addMixIn(DockerAuthDto.class, DockerAuthMixin.class);
        json = new JacksonJson(objectMapper);
    }

    @Test
    void name1() {
        ApiClient apiClient = new JdkApiClient(registryResolver, httpClient, new Authenticator(httpClient, json), json);

        ManifestDto manifest = apiClient.getManifest(Reference.of("alpine"));
        System.out.println(manifest);
    }

    @Test
    void name2() {
        ApiClient apiClient = new JdkApiClient(registryResolver, httpClient, new Authenticator(httpClient, json), json);

        ManifestDto manifest = apiClient.getManifest(Reference.of("ghcr.io/alexey-lapin/micronaut-proxy"));
        System.out.println(manifest);
    }

    @Test
    void name3() {
        ApiClient apiClient = new JdkApiClient(registryResolver, httpClient, new Authenticator(httpClient, json), json);

        RegistryOperations registryOperations = new DefaultRegistryOperations(apiClient);
        FileOperations fileOperations = new DefaultFileOperations(json);

        RegistryClient registryClient = new DefaultRegistryClient(registryOperations, fileOperations);

        registryClient.pull(Reference.of("ghcr.io/alexey-lapin/micronaut-proxy:0.0.5"), Paths.get("mic-prox.tar"));
    }

}