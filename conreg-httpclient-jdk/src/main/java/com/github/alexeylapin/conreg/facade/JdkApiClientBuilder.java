package com.github.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.TokenStore;
import com.gihtub.alexeylapin.conreg.facade.ApiClientBuilder;
import com.gihtub.alexeylapin.conreg.facade.WellKnownRegistries;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.github.alexeylapin.conreg.http.JdkApiClient;
import com.github.alexeylapin.conreg.http.LoggingHttpClient;

import java.net.http.HttpClient;

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
        this.jsonCodec = null;
        this.registryResolver = new WellKnownRegistries();
        this.authenticationProvider = null;
        this.tokenStore = null;
    }

    @Override
    public JdkApiClientBuilder jsonCodec(JsonCodec jsonCodec) {
        this.jsonCodec = jsonCodec;
        return this;
    }

    @Override
    public JdkApiClientBuilder registryResolver(RegistryResolver registryResolver) {
        this.registryResolver = registryResolver;
        return this;
    }

    @Override
    public JdkApiClientBuilder authenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        return this;
    }

    @Override
    public JdkApiClientBuilder tokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
        return this;
    }

    public JdkApiClientBuilder httpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    @Override
    public JdkApiClient build() {
        return new JdkApiClient(httpClient, registryResolver, jsonCodec, authenticationProvider, tokenStore);
    }

}
