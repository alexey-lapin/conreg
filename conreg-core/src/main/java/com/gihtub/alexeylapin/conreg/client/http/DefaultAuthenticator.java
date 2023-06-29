package com.gihtub.alexeylapin.conreg.client.http;

import java.util.Optional;

public class DefaultAuthenticator implements Authenticator {

    private final AuthenticationHolder authenticationHolder;

    public DefaultAuthenticator(AuthenticationHolder authenticationHolder) {
        this.authenticationHolder = authenticationHolder;
    }

    @Override
    public Optional<String> getForRegistry(String registry) {
        return authenticationHolder.getForRegistry(registry).map(value -> "Basic " + value);
    }

    @Override
    public String authenticate(String registry, String context) {
        return null;
    }

}
