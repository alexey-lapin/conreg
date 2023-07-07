package com.gihtub.alexeylapin.conreg.facade;

import com.gihtub.alexeylapin.conreg.image.Reference;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public interface RegistryClient {

    void pull(Reference reference, Path path);

    void pull(Reference reference, OutputStream outputStream);

    void push(Path path, Reference reference);

    void push(InputStream inputStream, Reference reference);

    void copy(Reference source, Reference target);

}
