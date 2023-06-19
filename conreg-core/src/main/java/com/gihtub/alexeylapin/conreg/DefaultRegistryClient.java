package com.gihtub.alexeylapin.conreg;

import com.gihtub.alexeylapin.conreg.image.Image;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.io.FileOperations;
import com.gihtub.alexeylapin.conreg.registry.RegistryOperations;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultRegistryClient implements RegistryClient {

    private final RegistryOperations registryOperations;
    private final FileOperations fileOperations;

    public DefaultRegistryClient(RegistryOperations registryOperations, FileOperations fileOperations) {
        this.registryOperations = registryOperations;
        this.fileOperations = fileOperations;
    }

    @Override
    public void pull(Reference reference, Path path) {
        try (OutputStream fis = Files.newOutputStream(path)) {
            pull(reference, fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pull(Reference reference, OutputStream outputStream) {
        Image image = registryOperations.pull(reference);
        fileOperations.save(image, outputStream);
    }

}
