package com.gihtub.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.TokenStore;
import com.gihtub.alexeylapin.conreg.facade.factory.JsonCodecFactory;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.gihtub.alexeylapin.conreg.registry.RegistryOperations;

import java.util.Optional;
import java.util.ServiceLoader;

public interface RegistryClients {

    static RegistryClient defaultClient() {
        return builder().build();
    }

    static RegistryClientBuilder builder() {
        return new RegistryClientBuilder();
    }

    class RegistryClientBuilder {

        private JsonCodec jsonCodec;
        private RegistryResolver registryResolver;
        private AuthenticationProvider authenticationProvider;
        private TokenStore tokenStore;
        private ApiClient apiClient;
        private RegistryOperations registryOperations;

        RegistryClient build() {
            if (jsonCodec == null) {
                for (JsonCodecFactory jsonCodecFactory : ServiceLoader.load(JsonCodecFactory.class)) {
                    Optional<JsonCodec> jsonCodecOptional = jsonCodecFactory.create();
                    if (jsonCodecOptional.isPresent()) {
                        jsonCodec = jsonCodecOptional.get();
                        break;
                    }
                }
                if (jsonCodec == null) {
                    throw new IllegalStateException("no json codec found");
                }
            }
            if (registryResolver == null) {
                registryResolver = new WellKnownRegistries();
            }
            if (apiClient == null) {

            }
            if (registryOperations == null) {

            }
            return new DefaultRegistryClient(jsonCodec, registryOperations);
        }

    }

}
