package com.gihtub.alexeylapin.conreg.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileOperations extends AutoCloseable {

    String MANIFEST = "manifest.json";

    static long copy(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, 8024);
    }

    static long copy(final InputStream input, final OutputStream output, final int buffersize) throws IOException {
        if (buffersize < 1) {
            throw new IllegalArgumentException("buffersize must be bigger than 0");
        }
        final byte[] buffer = new byte[buffersize];
        int n = 0;
        long count=0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
