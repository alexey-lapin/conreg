package com.gihtub.alexeylapin.conreg.image;

import com.gihtub.alexeylapin.conreg.io.ThrowingSupplier;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@Getter
public class Blob {

    private final Type type;
    private final String digest;
    private final Long size;
    private final ThrowingSupplier<InputStream> content;

    public Blob(Type type, String digest, Long size, ThrowingSupplier<InputStream> content) {
        this.digest = digest;
        this.type = type;
        this.size = size;
        this.content = content;
    }

    public String getName() {
        return digest.split(":")[1] + "." + type.ext;
    }

    public static Blob ofJson(String digest, Long size, ThrowingSupplier<InputStream> content) {
        return new Blob(Type.JSON, digest, size, content);
    }

    @Builder
    public static Blob ofTar(String digest, Long size, ThrowingSupplier<InputStream> content) {
        return new Blob(Type.TAR, digest, size, content);
    }

    @RequiredArgsConstructor
    public enum Type {

        JSON("json"),
        TAR("tar");

        public final String ext;

    }

}
