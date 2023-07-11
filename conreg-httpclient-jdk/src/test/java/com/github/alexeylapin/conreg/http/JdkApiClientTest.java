package com.github.alexeylapin.conreg.http;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.WellKnownFileAuthHolders;
import com.gihtub.alexeylapin.conreg.client.http.auth.FileAuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.NoopTokenStore;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDescriptor;
import com.gihtub.alexeylapin.conreg.client.http.dto.TokenDto;
import com.gihtub.alexeylapin.conreg.facade.WellKnownRegistries;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.gihtub.alexeylapin.conreg.json.jackson.JacksonJsonCodec;
import com.gihtub.alexeylapin.conreg.json.jackson.TokenDtoMixin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.net.URI;
import java.net.http.HttpClient;

class JdkApiClientTest {

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private RegistryResolver registryResolver;
    private HttpClient httpClient;
    private JsonCodec jsonCodec;
    private ApiClient apiClient;

    @BeforeEach
    void setUp() {
        Logger logger = LoggerFactory.getLogger(JdkApiClientTest.class);
        registryResolver = new WellKnownRegistries();

        HttpClient actualHttpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        httpClient = new LoggingHttpClient(actualHttpClient);

        ObjectMapper objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addMixIn(TokenDto.class, TokenDtoMixin.class);
        jsonCodec = new JacksonJsonCodec(objectMapper);

        FileAuthenticationProvider authenticationProvider = new WellKnownFileAuthHolders().create(jsonCodec).orElseThrow();
        apiClient = new JdkApiClient(httpClient, registryResolver, jsonCodec, authenticationProvider, new NoopTokenStore());
    }

    @Test
    void name1() {
        ManifestDescriptor manifest = apiClient.getManifest(Reference.of("alpine"));
        System.out.println(manifest);
    }

    @Test
    void name2() {
        ManifestDescriptor manifest = apiClient.getManifest(Reference.of("ghcr.io/alexey-lapin/micronaut-proxy"));
        System.out.println(manifest);
    }


    @Test
    void name5() {
        URI uri = apiClient.startPush(Reference.of("ghcr.io/alexey-lapin/micronaut-proxy:text"));
        System.out.println(uri);
    }

    @Test
    void name6() {
        apiClient.cancelPush(Reference.of("ghcr.io/alexey-lapin/micronaut-proxy:text"), URI.create("https://ghcr.io/v2/alexey-lapin/micronaut-proxy/blobs/upload/5bf5f70f-dd1d-4655-98f3-2137194de0e3"));
    }

    @Nested
    class Local {

        private final Reference reference = Reference.of("localhost:5000/alexey-lapin/micronaut-proxy:test-2");

        @Test
        void name1() {
            ManifestDescriptor manifest = apiClient.getManifest(reference);
            System.out.println(manifest);
        }

        @Test
        void name2() {
            URI uploadUri = apiClient.startPush(reference);
            apiClient.cancelPush(reference, uploadUri);
        }

        @Test
        void name3() {
            ManifestDescriptor manifestDescriptor = apiClient.getManifest(reference);
            apiClient.deleteManifest(reference);
        }

    }

}