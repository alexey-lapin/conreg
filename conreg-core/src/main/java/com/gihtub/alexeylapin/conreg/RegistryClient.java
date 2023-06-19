package com.gihtub.alexeylapin.conreg;

import com.gihtub.alexeylapin.conreg.image.Reference;

import java.io.OutputStream;
import java.nio.file.Path;

public interface RegistryClient {

    void pull(Reference reference, Path path);

    void pull(Reference reference, OutputStream outputStream);

}
