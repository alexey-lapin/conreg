package com.gihtub.alexeylapin.conreg.client.http.auth;

import lombok.Getter;
import lombok.NonNull;

import java.util.Base64;

public interface Auth {

    String getAuth();

    static Auth basic(String username, String password) {
        return new BasicAuth(username, password);
    }

    static Auth basic(String encoded) {
        return new BasicAuth(encoded);
    }

    static Auth bearer(String token) {
        return new BearerAuth(token);
    }

    @Getter
    class BasicAuth implements Auth {

        private final String auth;

        public BasicAuth(@NonNull String username, @NonNull String password) {
            this(Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
        }

        public BasicAuth(@NonNull String encoded) {
            this.auth = "Basic " + encoded;
        }

    }

    @Getter
    class BearerAuth implements Auth {

        private final String auth;

        public BearerAuth(@NonNull String token) {
            this.auth = "Bearer " + token;
        }

    }

}
