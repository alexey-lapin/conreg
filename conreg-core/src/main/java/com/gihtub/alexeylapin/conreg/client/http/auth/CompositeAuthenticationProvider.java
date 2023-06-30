package com.gihtub.alexeylapin.conreg.client.http.auth;

import java.util.Collection;
import java.util.Optional;

public class CompositeAuthenticationProvider implements AuthenticationProvider {

    private final Collection<AuthenticationProvider> providers;

    public CompositeAuthenticationProvider(Collection<AuthenticationProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Optional<String> getForRegistry(Registry registry) {
        return providers.stream()
                .map(provider -> provider.getForRegistry(registry))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

}
