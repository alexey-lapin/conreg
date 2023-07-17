package com.gihtub.alexeylapin.conreg.client.http.auth;

import java.util.Optional;

public interface TokenStore {

    void store(TokenKey key, Token token);

    Optional<Token> retrieve(TokenKey key);

}
