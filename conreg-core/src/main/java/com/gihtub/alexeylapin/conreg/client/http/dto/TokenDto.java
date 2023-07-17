package com.gihtub.alexeylapin.conreg.client.http.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Optional;

@Data
public class TokenDto {

    private String token;
    private String accessToken;
    private Integer expiresIn;
    private ZonedDateTime issuedAt;

    public Optional<String> getAccessToken() {
        return Optional.ofNullable(accessToken);
    }

    public Optional<Integer> getExpiresIn() {
        return Optional.ofNullable(expiresIn);
    }

    public Optional<ZonedDateTime> getIssuedAt() {
        return Optional.ofNullable(issuedAt);
    }

}
