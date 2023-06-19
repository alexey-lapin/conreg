package com.gihtub.alexeylapin.conreg.registry;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.dto.BlobDto;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.image.Blob;
import com.gihtub.alexeylapin.conreg.image.Image;
import com.gihtub.alexeylapin.conreg.image.Reference;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultRegistryOperations implements RegistryOperations {

    private final ApiClient apiClient;

    public DefaultRegistryOperations(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public Image pull(Reference reference) {
        ManifestDto manifest = apiClient.getManifest(reference);
        BlobDto config = manifest.getConfig();
        Blob configBlob = Blob.ofJson(config.getDigest(), config.getSize(),
                () -> apiClient.getBlob(reference, config.getDigest()));
        List<Blob> layerBlobs = manifest.getLayers().stream()
                .map(item -> Blob.ofTar(item.getDigest(), item.getSize(), () -> apiClient.getBlob(reference, item.getDigest())))
                .collect(Collectors.toList());
        return new Image(reference, configBlob, layerBlobs);
    }

}
