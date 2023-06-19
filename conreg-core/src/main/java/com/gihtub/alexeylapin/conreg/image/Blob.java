package com.gihtub.alexeylapin.conreg.image;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;
import java.util.function.Supplier;

@Getter
public class Blob {

    private final Type type;
    private final String digest;
    private final Long size;
    private final Supplier<InputStream> content;

    public Blob(Type type, String digest, Long size, Supplier<InputStream> content) {
        this.digest = digest;
        this.type = type;
        this.size = size;
        this.content = content;
    }

    public String getName() {
        return digest.split(":")[1] + "." + type.ext;
    }

    public static Blob ofJson(String digest, Long size, Supplier<InputStream> content) {
        return new Blob(Type.JSON, digest, size, content);
    }

    @Builder
    public static Blob ofTar(String digest, Long size, Supplier<InputStream> content) {
        return new Blob(Type.TAR, digest, size, content);
    }

    public enum Type {
        JSON("json"),
        TAR("tar");

        public final String ext;

        Type(String ext) {
            this.ext = ext;
        }

    }

}
