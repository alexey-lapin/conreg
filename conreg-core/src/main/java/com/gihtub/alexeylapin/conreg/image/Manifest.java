package com.gihtub.alexeylapin.conreg.image;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class Manifest {

    private final String config;
    private final List<String> repoTags;
    private final List<String> layers;

}
