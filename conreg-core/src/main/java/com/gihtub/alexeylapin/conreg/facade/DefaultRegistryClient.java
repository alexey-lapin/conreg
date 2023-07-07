package com.gihtub.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.image.Image;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.io.TarInputStreamImageLoader;
import com.gihtub.alexeylapin.conreg.io.TarOutputStreamImageSaver;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.gihtub.alexeylapin.conreg.registry.RegistryOperations;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultRegistryClient implements RegistryClient {

    private final JsonCodec jsonCodec;
    private final RegistryOperations registryOperations;

    public DefaultRegistryClient(JsonCodec jsonCodec, RegistryOperations registryOperations) {
        this.jsonCodec = jsonCodec;
        this.registryOperations = registryOperations;
    }

    @SneakyThrows
    @Override
    public void pull(Reference reference, Path path) {
        try (OutputStream fos = Files.newOutputStream(path)) {
            pull(reference, fos);
        }
    }

    @SneakyThrows
    @Override
    public void pull(Reference reference, OutputStream outputStream) {
        Image image = registryOperations.pull(reference);
        try (TarOutputStreamImageSaver imageSaver = new TarOutputStreamImageSaver(jsonCodec, outputStream)) {
            imageSaver.save(image);
        }
    }

    @SneakyThrows
    @Override
    public void push(Path path, Reference reference) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            push(inputStream, reference);
        }
    }

    @SneakyThrows
    @Override
    public void push(InputStream inputStream, Reference reference) {
        try (TarInputStreamImageLoader imageLoader =
                     new TarInputStreamImageLoader(jsonCodec, inputStream, Files.createTempDirectory("conreg-"))) {
            Image image = imageLoader.load();
            registryOperations.push(reference, image);
        }
    }

    @Override
    public void copy(Reference source, Reference target) {
        Image image = registryOperations.pull(source);
        registryOperations.push(target, image);
    }

}
