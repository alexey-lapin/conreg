package com.gihtub.alexeylapin.conreg.client.http.dto;

import lombok.Data;

import java.util.List;

@Data
public class ManifestDto {

    private Integer schemaVersion;
    private String mediaType;
    private BlobDto config;
    private List<BlobDto> layers;

}
