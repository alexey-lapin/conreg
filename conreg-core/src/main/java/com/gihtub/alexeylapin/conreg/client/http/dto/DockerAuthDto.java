package com.gihtub.alexeylapin.conreg.client.http.dto;

import lombok.Data;

@Data
public class DockerAuthDto {

    private String token;
    private String accessToken;
    private Integer expiresIn;
    private String issuedAt;

}
