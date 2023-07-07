package com.gihtub.alexeylapin.conreg.registry;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.dto.BlobDescriptor;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDescriptor;
import com.gihtub.alexeylapin.conreg.image.Blob;
import com.gihtub.alexeylapin.conreg.image.Image;
import com.gihtub.alexeylapin.conreg.image.Reference;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultRegistryOperations implements RegistryOperations {

    private final ApiClient apiClient;

    public DefaultRegistryOperations(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public Image pull(Reference reference) {
        ManifestDescriptor manifestDescriptor = apiClient.getManifest(reference);
        BlobDescriptor config = manifestDescriptor.getConfig();
        Blob configBlob = Blob.ofJson(config.getDigest(), config.getSize(),
                () -> apiClient.getBlob(reference, config.getDigest()));
        List<Blob> layerBlobs = manifestDescriptor.getLayers().stream()
                .map(item -> Blob.ofTar(item.getDigest(), item.getSize(),
                        () -> apiClient.getBlob(reference, item.getDigest())))
                .collect(Collectors.toList());
        return new Image(reference, configBlob, layerBlobs);
    }

    @Override
    public void push(Reference reference, Image image) {
        for (Blob blob : image.getBlobs()) {
            if (!apiClient.isBlobExists(reference, blob.getDigest())) {
                URI uploadUri = apiClient.startPush(reference);
                apiClient.putBlob(reference, uploadUri, blob.getDigest(), blob);
            }
        }
        apiClient.putManifest(reference, image.getManifestDescriptor());
    }

}
