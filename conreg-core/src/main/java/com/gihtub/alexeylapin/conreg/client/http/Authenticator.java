package com.gihtub.alexeylapin.conreg.client.http;

import java.util.Optional;

public interface Authenticator {

    Optional<String> getForRegistry(String registry);

    String authenticate(String registry, String context);

}
