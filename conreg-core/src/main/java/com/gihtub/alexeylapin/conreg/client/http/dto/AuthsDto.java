package com.gihtub.alexeylapin.conreg.client.http.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AuthsDto {

    private Map<String, AuthDto> auths = new HashMap<>();

    @Data
    public static class AuthDto {

        private String auth;

    }

}
