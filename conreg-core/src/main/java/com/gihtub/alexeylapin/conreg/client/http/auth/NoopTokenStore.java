package com.gihtub.alexeylapin.conreg.client.http.auth;

import java.util.Optional;

public class NoopTokenStore implements TokenStore {

    @Override
    public Optional<Auth> retrieve(TokenKey key) {
        return Optional.empty();
    }

    @Override
    public Auth store(TokenKey key, Token token) {
        return null;
    }

}
