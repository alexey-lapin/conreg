package com.gihtub.alexeylapin.conreg.client.http.dto;

import lombok.Data;

@Data
public class BlobDto {

    private String mediaType;
    private Long size;
    private String digest;

}
