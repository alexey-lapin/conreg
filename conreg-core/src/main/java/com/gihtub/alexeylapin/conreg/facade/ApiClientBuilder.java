package com.gihtub.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.TokenStore;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;

public interface ApiClientBuilder<T extends ApiClientBuilder<T>> {

    T jsonCodec(JsonCodec jsonCodec);

    T registryResolver(RegistryResolver registryResolver);

    T authenticationProvider(AuthenticationProvider authenticationProvider);

    T tokenStore(TokenStore tokenStore);

    ApiClient build();

}
