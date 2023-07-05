package com.gihtub.alexeylapin.conreg.io;

import com.gihtub.alexeylapin.conreg.image.Image;

public interface ImageLoader extends AutoCloseable {

    Image load();

}
