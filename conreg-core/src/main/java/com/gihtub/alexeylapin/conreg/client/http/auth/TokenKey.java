package com.gihtub.alexeylapin.conreg.client.http.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class TokenKey {

    private final String service;
    private final Scope scope;

    public static TokenKey of(String service, Scope scope) {
        return new TokenKey(service, scope);
    }

}
