package com.gihtub.alexeylapin.conreg.client.http.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ConfigDto {

    private Map<String, AuthConfigDto> auths = new HashMap<>();

    @Data
    public static class AuthConfigDto {

        private String auth;

    }

}
