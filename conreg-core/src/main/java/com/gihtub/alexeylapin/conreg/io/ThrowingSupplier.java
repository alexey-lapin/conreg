package com.gihtub.alexeylapin.conreg.io;

import java.io.IOException;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<T> {

    T get() throws IOException;

    default Supplier<T> unchecked() {
        return this::sneakyThrow;
    }

    @SuppressWarnings("unchecked")
    default <X extends Throwable> T sneakyThrow() throws X {
        try {
            return get();
        } catch (Throwable e) {
            throw (X) e;
        }
    }

}
