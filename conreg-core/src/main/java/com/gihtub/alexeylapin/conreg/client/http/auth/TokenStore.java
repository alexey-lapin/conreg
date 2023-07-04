package com.gihtub.alexeylapin.conreg.client.http.auth;

import java.util.Optional;

public interface TokenStore {

    Optional<Auth> retrieve(TokenKey key);

    Auth store(TokenKey key, Token token);

}
