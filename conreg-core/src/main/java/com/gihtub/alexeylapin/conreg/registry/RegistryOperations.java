package com.gihtub.alexeylapin.conreg.registry;

import com.gihtub.alexeylapin.conreg.image.Image;
import com.gihtub.alexeylapin.conreg.image.Reference;

public interface RegistryOperations {

    Image pull(Reference reference);

    void push(Reference reference, Image image);

}
