package com.gihtub.alexeylapin.conreg.client.http.auth;

import com.gihtub.alexeylapin.conreg.client.http.dto.ConfigDto;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public class FileAuthenticationProvider implements AuthenticationProvider {

    private final JsonCodec jsonCodec;
    private final Set<Path> paths;

    public FileAuthenticationProvider(JsonCodec jsonCodec, Set<Path> paths) {
        this.jsonCodec = jsonCodec;
        this.paths = paths;
    }

    @SneakyThrows
    @Override
    public Optional<String> getForRegistry(Registry registry) {
        for (Path path : paths) {
            if (Files.exists(path)) {
                String string = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                ConfigDto configDto = jsonCodec.decode(string, ConfigDto.class);
                return Optional.ofNullable(configDto.getAuths().get(registry.getName()))
                        .map(ConfigDto.AuthConfigDto::getAuth)
                        .map(encoded -> "Basic " + encoded);
            }
        }
        return Optional.empty();
    }

}
