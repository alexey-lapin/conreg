package com.gihtub.alexeylapin.conreg.io;

import com.gihtub.alexeylapin.conreg.image.Image;

import java.io.IOException;

public interface ImageLoader extends FileOperations {

    Image load() throws IOException;

}
