package com.gihtub.alexeylapin.conreg.client.http.auth;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleAuthenticationProvider implements AuthenticationProvider {

    private final Map<Registry, Auth> auths;

    public SimpleAuthenticationProvider(Map<Registry, Auth> auths) {
        this.auths = new ConcurrentHashMap<>(auths);
    }

    @Override
    public Optional<String> getForRegistry(Registry registry) {
        return Optional.ofNullable(auths.get(registry)).map(Auth::getValue);
    }

}
