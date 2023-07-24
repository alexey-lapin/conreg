package com.github.alexeylapin.conreg.http;


import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.WellKnownFileAuthenticationProviderFactory;
import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.DefaultTokenStore;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDescriptor;
import com.gihtub.alexeylapin.conreg.facade.WellKnownRegistries;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.gihtub.alexeylapin.conreg.json.jackson.JacksonJsonCodecFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;

class JdkApiClientTest {

    private ApiClient apiClient;

    @BeforeEach
    void setUp() {
        RegistryResolver registryResolver = new WellKnownRegistries();
        HttpClient delegateHttpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        HttpClient httpClient = new LoggingHttpClient(delegateHttpClient);
        JsonCodec jsonCodec = new JacksonJsonCodecFactory().create().orElseThrow();
        AuthenticationProvider authenticationProvider = new WellKnownFileAuthenticationProviderFactory().create(jsonCodec).orElseThrow();
        apiClient = new JdkApiClient(httpClient, registryResolver, jsonCodec, authenticationProvider, new DefaultTokenStore());
    }

    @Test
    void name1() {
        Reference reference = Reference.of("alpine");
        ManifestDescriptor manifestDescriptor = apiClient.getManifest(reference);
        System.out.println(manifestDescriptor);
    }

    @Test
    void name2() {
        Reference reference = Reference.of("ghcr.io/alexey-lapin/micronaut-proxy");
        ManifestDescriptor manifestDescriptor = apiClient.getManifest(reference);
        System.out.println(manifestDescriptor);
    }


    @Test
    void name5() {
        Reference reference = Reference.of("ghcr.io/alexey-lapin/micronaut-proxy:text");
        URI uri = apiClient.startPush(reference);
        System.out.println(uri);
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