package com.gihtub.alexeylapin.conreg.image;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public class Image {

    private final Reference reference;
    private final Blob config;
    private final List<Blob> layers;

    public Image(Reference reference, Blob config, List<Blob> layers) {
        this.reference = reference;
        this.config = config;
        this.layers = Collections.unmodifiableList(new ArrayList<>(layers));
    }

    public List<Blob> getBlobs() {
        ArrayList<Blob> blobs = new ArrayList<>(layers);
        blobs.add(config);
        return Collections.unmodifiableList(blobs);
    }

    public Manifest getManifest() {
        return new Manifest(config.getName(),
                Collections.singletonList(reference.toString()),
                layers.stream().map(Blob::getName).collect(Collectors.toList()));
    }

}
