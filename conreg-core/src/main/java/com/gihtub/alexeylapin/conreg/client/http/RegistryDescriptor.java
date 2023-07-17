package com.gihtub.alexeylapin.conreg.client.http;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@RequiredArgsConstructor
@Builder
@Getter
public class RegistryDescriptor {

    private final String canonicalName;
    private final String serviceName;
    private final URI serviceUri;

}
