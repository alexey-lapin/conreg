package com.gihtub.alexeylapin.conreg.client.http.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Builder
@Getter
public class Token {

    @NonNull
    private final TokenKey key;

    @NonNull
    private final String token;
    private final String accessToken;
    private final Integer expiresIn;
    private final ZonedDateTime issuedAt;

    public Optional<String> getAccessToken() {
        return Optional.ofNullable(accessToken);
    }

    public Optional<Integer> getExpiresIn() {
        return Optional.ofNullable(expiresIn);
    }

}
