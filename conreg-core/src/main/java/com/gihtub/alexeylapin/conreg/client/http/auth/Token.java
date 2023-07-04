package com.gihtub.alexeylapin.conreg.client.http.auth;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Token {

    @NonNull
    private final TokenKey key;
    @NonNull
    private final String token;
    private final String accessToken;
    private final Integer expiresIn;
    private final String issuedAt;

}
