package com.github.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.WellKnownFileAuthenticationProviderFactory;
import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.CompositeAuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.DefaultTokenStore;
import com.gihtub.alexeylapin.conreg.client.http.auth.TokenStore;
import com.gihtub.alexeylapin.conreg.facade.ApiClientBuilder;
import com.gihtub.alexeylapin.conreg.facade.WellKnownRegistries;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.github.alexeylapin.conreg.http.JdkApiClient;
import com.github.alexeylapin.conreg.http.LoggingHttpClient;
import lombok.NonNull;

import java.net.http.HttpClient;
import java.util.Collections;
import java.util.Objects;

public class JdkApiClientBuilder implements ApiClientBuilder<JdkApiClientBuilder> {

    private HttpClient httpClient;
    private JsonCodec jsonCodec;
    private RegistryResolver registryResolver;
    private AuthenticationProvider authenticationProvider;
    private TokenStore tokenStore;

    public JdkApiClientBuilder() {
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        this.httpClient = new LoggingHttpClient(httpClient);
        this.registryResolver = new WellKnownRegistries();
        this.tokenStore = new DefaultTokenStore();
    }

    @Override
    public JdkApiClientBuilder jsonCodec(@NonNull JsonCodec jsonCodec) {
        this.jsonCodec = jsonCodec;
        return this;
    }

    @Override
    public JdkApiClientBuilder registryResolver(@NonNull RegistryResolver registryResolver) {
        this.registryResolver = registryResolver;
        return this;
    }

    @Override
    public JdkApiClientBuilder authenticationProvider(@NonNull AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        return this;
    }

    @Override
    public JdkApiClientBuilder tokenStore(@NonNull TokenStore tokenStore) {
        this.tokenStore = tokenStore;
        return this;
    }

    public JdkApiClientBuilder httpClient(@NonNull HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    @Override
    public JdkApiClient build() {
        Objects.requireNonNull(jsonCodec, "jsonCodec must not be null");
        if (authenticationProvider == null) {
            authenticationProvider = new WellKnownFileAuthenticationProviderFactory().create(jsonCodec)
                    .orElseGet(() -> new CompositeAuthenticationProvider(Collections.emptyList()));
        }
        return new JdkApiClient(httpClient, registryResolver, jsonCodec, authenticationProvider, tokenStore);
    }

}
