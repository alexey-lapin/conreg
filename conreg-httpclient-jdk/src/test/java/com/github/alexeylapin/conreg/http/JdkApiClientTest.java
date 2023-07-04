package com.github.alexeylapin.conreg.http;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihtub.alexeylapin.conreg.DefaultRegistryClient;
import com.gihtub.alexeylapin.conreg.RegistryClient;
import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.WellKnownFileAuthHolders;
import com.gihtub.alexeylapin.conreg.client.http.WellKnownRegistries;
import com.gihtub.alexeylapin.conreg.client.http.auth.FileAuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.NoopTokenStore;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.client.http.dto.TokenDto;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.io.DefaultFileOperations;
import com.gihtub.alexeylapin.conreg.io.FileOperations;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.gihtub.alexeylapin.conreg.json.jackson.TokenDtoMixin;
import com.gihtub.alexeylapin.conreg.json.jackson.JacksonJsonCodec;
import com.gihtub.alexeylapin.conreg.registry.DefaultRegistryOperations;
import com.gihtub.alexeylapin.conreg.registry.RegistryOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.net.http.HttpClient;
import java.nio.file.Paths;

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
        apiClient = new JdkApiClient(registryResolver, httpClient, jsonCodec, authenticationProvider, new NoopTokenStore());
    }

    @Test
    void name1() {
        ManifestDto manifest = apiClient.getManifest(Reference.of("alpine"));
        System.out.println(manifest);
    }

    @Test
    void name2() {
        ManifestDto manifest = apiClient.getManifest(Reference.of("ghcr.io/alexey-lapin/micronaut-proxy"));
        System.out.println(manifest);
    }

    @Test
    void name3() {
        RegistryOperations registryOperations = new DefaultRegistryOperations(apiClient);
        FileOperations fileOperations = new DefaultFileOperations(jsonCodec);

        RegistryClient registryClient = new DefaultRegistryClient(registryOperations, fileOperations);

        registryClient.pull(Reference.of("ghcr.io/alexey-lapin/micronaut-proxy:0.0.5"), Paths.get("mic-prox.tar"));
    }

    @Test
    void name4() {
        RegistryOperations registryOperations = new DefaultRegistryOperations(apiClient);
        FileOperations fileOperations = new DefaultFileOperations(jsonCodec);

        RegistryClient registryClient = new DefaultRegistryClient(registryOperations, fileOperations);

        registryClient.push(Paths.get("mic-prox.tar"), Reference.of("ghcr.io/alexey-lapin/micronaut-proxy:0.0.5"));
    }

}