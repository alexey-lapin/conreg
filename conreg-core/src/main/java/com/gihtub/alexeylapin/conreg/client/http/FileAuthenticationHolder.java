package com.gihtub.alexeylapin.conreg.client.http;

import com.gihtub.alexeylapin.conreg.client.http.dto.AuthsDto;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public class FileAuthenticationHolder implements AuthenticationHolder {

    private final JsonCodec jsonCodec;
    private final Set<Path> paths;

    public FileAuthenticationHolder(JsonCodec jsonCodec, Set<Path> paths) {
        this.jsonCodec = jsonCodec;
        this.paths = paths;
    }

    @SneakyThrows
    @Override
    public Optional<String> getForRegistry(String registry) {
        for (Path path : paths) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Files.copy(path, out);
            AuthsDto authsDto = jsonCodec.decode(new String(out.toByteArray(), StandardCharsets.UTF_8), AuthsDto.class);
            AuthsDto.AuthDto authDto = authsDto.getAuths().get(registry);
            if (authDto != null && authDto.getAuth() != null) {
                return Optional.of(authDto.getAuth());
            }
        }
        return Optional.empty();
    }

}
