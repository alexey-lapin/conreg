package com.gihtub.alexeylapin.conreg.client.http.auth;

import java.util.Optional;

public class NoopTokenStore implements TokenStore {

    @Override
    public void store(TokenKey key, Token token) {
    }

    @Override
    public Optional<Token> retrieve(TokenKey key) {
        return Optional.empty();
    }

}
