package com.gihtub.alexeylapin.conreg.client.http.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Data
public class ManifestDescriptor {

    private Integer schemaVersion;
    private String mediaType;
    private BlobDescriptor config;
    private List<BlobDescriptor> layers;

    public String getDigest() {
        return config.getDigest();
    }

}
