package com.gihtub.alexeylapin.conreg.io;

import com.gihtub.alexeylapin.conreg.image.Image;

import java.io.IOException;

public interface ImageSaver extends FileOperations {

    void save(Image image) throws IOException;

}
