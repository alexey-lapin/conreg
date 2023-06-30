package com.gihtub.alexeylapin.conreg.client.http.auth;

import java.util.Optional;

public interface AuthenticationProvider {

    Optional<String> getForRegistry(Registry registry);

}
