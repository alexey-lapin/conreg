package com.gihtub.alexeylapin.conreg.image;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@RequiredArgsConstructor
@Getter
public class Manifest {

    private final String config;
    private final List<String> repoTags;
    private final List<String> layers;

}
