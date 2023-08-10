package com.gihtub.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.facade.factory.JsonCodecFactory;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.gihtub.alexeylapin.conreg.registry.DefaultRegistryOperations;
import com.gihtub.alexeylapin.conreg.registry.RegistryOperations;
import lombok.NonNull;

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
        private ApiClient apiClient;
        private RegistryOperations registryOperations;

        public RegistryClientBuilder jsonCodec(@NonNull JsonCodec jsonCodec) {
            this.jsonCodec = jsonCodec;
            return this;
        }

        public RegistryClientBuilder apiClient(@NonNull ApiClient apiClient) {
            this.apiClient = apiClient;
            return this;
        }

        public RegistryClientBuilder registryOperations(@NonNull RegistryOperations registryOperations) {
            this.registryOperations = registryOperations;
            return this;
        }

        public RegistryClient build() {
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
            if (apiClient == null) {
                for (ApiClientBuilder<?> apiClientBuilder : ServiceLoader.load(ApiClientBuilder.class)) {
                    apiClient = apiClientBuilder.jsonCodec(jsonCodec).build();
                    break;
                }
            }
            if (registryOperations == null) {
                registryOperations = new DefaultRegistryOperations(apiClient);
            }
            return new DefaultRegistryClient(jsonCodec, registryOperations);
        }

    }

}
