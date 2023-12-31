package com.gihtub.alexeylapin.conreg.client.http.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

public interface Registry {

    String getName();

    static Registry of(String name) {
        return new NamedRegistry(name);
    }

    @Getter
    @EqualsAndHashCode
    class NamedRegistry implements Registry {

        private final String name;

        public NamedRegistry(@NonNull String name) {
            this.name = name;
        }

    }

}
