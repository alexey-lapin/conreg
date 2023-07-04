package com.gihtub.alexeylapin.conreg.client.http.auth;

import java.util.Map;
import java.util.Optional;

public class SimpleTokenStore implements TokenStore {

    private final Map<TokenKey, Token> tokens;

    public SimpleTokenStore() {
        tokens = null;
    }

    @Override
    public Optional<Auth> retrieve(TokenKey key) {
        return Optional.empty();
    }

    @Override
    public Auth store(TokenKey key, Token token) {
        return null;
    }

}
