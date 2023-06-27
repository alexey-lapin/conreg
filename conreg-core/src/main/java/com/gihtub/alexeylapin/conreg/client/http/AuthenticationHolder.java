package com.gihtub.alexeylapin.conreg.client.http;

import java.util.Optional;

public interface AuthenticationHolder {

    Optional<String> getForRegistry(String registry);

}
