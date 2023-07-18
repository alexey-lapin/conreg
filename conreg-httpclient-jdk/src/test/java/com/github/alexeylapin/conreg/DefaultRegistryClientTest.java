package com.github.alexeylapin.conreg;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.WellKnownFileAuthHolders;
import com.gihtub.alexeylapin.conreg.client.http.auth.DefaultTokenStore;
import com.gihtub.alexeylapin.conreg.client.http.auth.FileAuthenticationProvider;
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

    private RegistryResolver registryResolver;
    private HttpClient httpClient;
    private JsonCodec jsonCodec;
    private ApiClient apiClient;
    private RegistryClient registryClient;

    @BeforeEach
    void setUp() {
        registryResolver = new WellKnownRegistries();

        HttpClient actualHttpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        httpClient = new LoggingHttpClient(actualHttpClient);

        jsonCodec = new JacksonJsonCodecFactory().create().orElseThrow();

        FileAuthenticationProvider authenticationProvider = new WellKnownFileAuthHolders().create(jsonCodec).orElseThrow();
        apiClient = new JdkApiClient(httpClient, registryResolver, jsonCodec, authenticationProvider, new DefaultTokenStore());

        RegistryOperations registryOperations = new DefaultRegistryOperations(apiClient);

        registryClient = new DefaultRegistryClient(jsonCodec, registryOperations);
    }

    @Test
    void name1() {
        registryClient.pull(Reference.of("alpine"), Paths.get("alpine.tar"));
    }

    @Test
    void name2() {
        registryClient.pull(Reference.of("ghcr.io/alexey-lapin/micronaut-proxy:0.0.5"), Paths.get("mic-prox.tar"));
    }

    @Test
    void name3() {
        registryClient.pull(Reference.of("localhost:5000/alexey-lapin/micronaut-proxy:0.0.5"), Paths.get("mic-prox.tar"));
    }

    @Test
    void name4() {
        registryClient.push(Paths.get("mic-prox.tar"), Reference.of("localhost:5000/alexey-lapin/micronaut-proxy:test-2"));
    }

    @Test
    void name5() {
        registryClient.copy(Reference.of("alpine"), Reference.of("localhost:5000/alpine"));
    }

    @Test
    void name6() {
        RegistryClient client = RegistryClients.defaultClient();
        System.out.println();
    }

}
