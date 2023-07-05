package com.gihtub.alexeylapin.conreg.client.http.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Data
public class BlobDescriptor {

    private String mediaType;
    private Long size;
    private String digest;

}
