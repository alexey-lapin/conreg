package com.gihtub.alexeylapin.conreg.model;

import lombok.Value;

@Value
public class Reference {

    private final String endpoint;
    private final String name;
    private final String tag;
    private final String digest;

}