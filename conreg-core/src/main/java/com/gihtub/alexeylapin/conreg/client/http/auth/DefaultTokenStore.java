package com.gihtub.alexeylapin.conreg.client.http.auth;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultTokenStore implements TokenStore {

    private final static int DEFAULT_EXPIRATION_SECONDS = 60;

    private final Map<TokenKey, Token> tokens;
    private final int expirationSeconds;

    public DefaultTokenStore() {
        this(DEFAULT_EXPIRATION_SECONDS);
    }

    public DefaultTokenStore(int expirationSeconds) {
        this.tokens = new ConcurrentHashMap<>();
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public void store(TokenKey key, Token token) {
        tokens.put(key, token);
    }

    @Override
    public Optional<Token> retrieve(TokenKey key) {
        Token token = tokens.get(key);
        if (token == null) {
            return Optional.empty();
        }
        boolean isExpired = token.getIssuedAt()
                .plusSeconds(token.getExpiresIn().orElse(expirationSeconds))
                .isBefore(ZonedDateTime.now());
        if (isExpired) {
            tokens.remove(key);
        } else {
            return Optional.of(token);
        }
        return Optional.empty();
    }

}
