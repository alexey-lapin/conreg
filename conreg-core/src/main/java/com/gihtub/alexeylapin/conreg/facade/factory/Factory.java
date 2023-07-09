package com.gihtub.alexeylapin.conreg.facade.factory;

import java.util.Optional;

public interface Factory<T> {

    Optional<T> create();

}
