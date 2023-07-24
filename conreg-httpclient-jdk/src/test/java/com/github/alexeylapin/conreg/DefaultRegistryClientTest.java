package com.github.alexeylapin.conreg;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.WellKnownFileAuthenticationProviderFactory;
import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.DefaultTokenStore;
import com.gihtub.alexeylapin.conreg.facade.DefaultRegistryClient;
import com.gihtub.alexeylapin.conreg.facade.RegistryClient;
import com.gihtub.alexeylapin.conreg.facade.RegistryClients;
import com.gihtub.alexeylapin.conreg.facade.WellKnownRegistries;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.gihtub.alexeylapin.conreg.json.jackson.JacksonJsonCodecFactory;
import com.gihtub.alexeylapin.conreg.registry.DefaultRegistryOperations;
import com.gihtub.alexeylapin.conreg.registry.RegistryOperations;
import com.github.alexeylapin.conreg.http.JdkApiClient;
import com.github.alexeylapin.conreg.http.LoggingHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.nio.file.Paths;

public class DefaultRegistryClientTest {

    private RegistryClient registryClient;

    @BeforeEach
    void setUp() {
        RegistryResolver registryResolver = new WellKnownRegistries();
        HttpClient delegateHttpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        HttpClient httpClient = new LoggingHttpClient(delegateHttpClient);
        JsonCodec jsonCodec = new JacksonJsonCodecFactory().create().orElseThrow();
        AuthenticationProvider authenticationProvider = new WellKnownFileAuthenticationProviderFactory().create(jsonCodec).orElseThrow();
        ApiClient apiClient = new JdkApiClient(httpClient, registryResolver, jsonCodec, authenticationProvider, new DefaultTokenStore());
        RegistryOperations registryOperations = new DefaultRegistryOperations(apiClient);
        registryClient = new DefaultRegistryClient(jsonCodec, registryOperations);
    }

    @Test
    void name1() {
        Reference reference = Reference.of("alpine");
        registryClient.pull(reference, Paths.get("alpine.tar"));
    }

    @Test
    void name2() {
        Reference reference = Reference.of("ghcr.io/alexey-lapin/micronaut-proxy:0.0.5");
        registryClient.pull(reference, Paths.get("mic-prox.tar"));
    }

    @Test
    void name3() {
        Reference reference = Reference.of("localhost:5000/alexey-lapin/micronaut-proxy:0.0.5");
        registryClient.pull(reference, Paths.get("mic-prox.tar"));
    }

    @Test
    void name4() {
        Reference reference = Reference.of("localhost:5000/alexey-lapin/micronaut-proxy:test-2");
        registryClient.push(Paths.get("mic-prox.tar"), reference);
    }

    @Test
    void name5() {
        Reference reference = Reference.of("alpine");
        registryClient.copy(reference, Reference.of("localhost:5000/alpine"));
    }

    @Test
    void name6() {
        RegistryClient client = RegistryClients.defaultClient();
        System.out.println();
    }

}
