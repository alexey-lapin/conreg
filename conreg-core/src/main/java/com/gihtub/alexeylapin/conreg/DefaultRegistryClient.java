package com.gihtub.alexeylapin.conreg;

import com.gihtub.alexeylapin.conreg.image.Image;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.io.FileImageLoader;
import com.gihtub.alexeylapin.conreg.io.FileOperations;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.gihtub.alexeylapin.conreg.registry.RegistryOperations;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultRegistryClient implements RegistryClient {

    private final JsonCodec jsonCodec;
    private final RegistryOperations registryOperations;
    private final FileOperations fileOperations;

    public DefaultRegistryClient(JsonCodec jsonCodec, RegistryOperations registryOperations, FileOperations fileOperations) {
        this.jsonCodec = jsonCodec;
        this.registryOperations = registryOperations;
        this.fileOperations = fileOperations;
    }

    @Override
    public void pull(Reference reference, Path path) {
        try (OutputStream fos = Files.newOutputStream(path)) {
            pull(reference, fos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pull(Reference reference, OutputStream outputStream) {
        Image image = registryOperations.pull(reference);
        fileOperations.save(image, outputStream);
    }

    @Override
    public void push(Path path, Reference reference) {
        try (FileImageLoader imageLoader = new FileImageLoader(jsonCodec, path, Files.createTempDirectory("conreg-"))) {
            Image image = imageLoader.load();
            registryOperations.push(reference, image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void push(InputStream inputStream, Reference reference) {
//        Image image = fileOperations.load(inputStream);
//        registryOperations.push(reference, image);
    }

}
