package com.gihtub.alexeylapin.conreg.image;

import com.gihtub.alexeylapin.conreg.client.http.dto.BlobDescriptor;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDescriptor;
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

    public ManifestDescriptor getManifestDescriptor() {
        return new ManifestDescriptor(2,
                "application/vnd.docker.distribution.manifest.v2+json",
                new BlobDescriptor("application/vnd.docker.container.image.v1+json", config.getSize(), config.getDigest()),
                layers.stream()
                        .map(item -> new BlobDescriptor("application/vnd.docker.image.rootfs.diff.tar.gzip", item.getSize(), item.getDigest()))
                        .collect(Collectors.toList()));
    }

    public Manifest getManifest() {
        return new Manifest(config.getName(),
                Collections.singletonList(reference.toString()),
                layers.stream().map(Blob::getName).collect(Collectors.toList()));
    }

}
