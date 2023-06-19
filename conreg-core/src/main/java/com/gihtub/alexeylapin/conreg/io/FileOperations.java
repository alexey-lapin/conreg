package com.gihtub.alexeylapin.conreg.io;

import com.gihtub.alexeylapin.conreg.image.Image;

import java.io.OutputStream;

public interface FileOperations {

    String MANIFEST = "manifest.json";

    void save(Image image, OutputStream outputStream);

}
