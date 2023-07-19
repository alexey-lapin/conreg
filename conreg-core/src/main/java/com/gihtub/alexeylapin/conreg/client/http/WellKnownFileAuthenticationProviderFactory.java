package com.gihtub.alexeylapin.conreg.client.http;

import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.FileAuthenticationProvider;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class WellKnownFileAuthenticationProviderFactory {

    private final Set<Path> paths;

    public WellKnownFileAuthenticationProviderFactory() {
        this(defaultPaths());
    }

    public WellKnownFileAuthenticationProviderFactory(Set<Path> paths) {
        this.paths = paths;
    }

    public Optional<AuthenticationProvider> create(JsonCodec jsonCodec) {
        Set<Path> existingPaths = paths.stream()
                .filter(Files::exists)
                .collect(Collectors.toSet());
        return existingPaths.isEmpty() ?
                Optional.empty() :
                Optional.of(new FileAuthenticationProvider(jsonCodec, existingPaths));
    }

    public static Set<Path> defaultPaths() {
        Set<Path> paths = new HashSet<>();
        paths.add(Paths.get(System.getProperty("user.home"), ".docker", "config.json"));
        paths.add(Paths.get(System.getProperty("user.home"), ".config", "containers", "auth.json"));
        return paths;
    }

}
