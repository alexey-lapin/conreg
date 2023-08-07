package com.gihtub.alexeylapin.conreg.json.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gihtub.alexeylapin.conreg.client.http.dto.TokenDto;
import com.gihtub.alexeylapin.conreg.facade.factory.JsonCodecFactory;
import com.gihtub.alexeylapin.conreg.image.Manifest;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;

import java.util.Optional;

public class JacksonJsonCodecFactory implements JsonCodecFactory {

    private final boolean enabled;

    public JacksonJsonCodecFactory() {
        boolean isJacksonLoadable;
        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            Class.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule");
            isJacksonLoadable = true;
        } catch (ClassNotFoundException e) {
            isJacksonLoadable = false;
        }
        this.enabled = isJacksonLoadable;
    }

    @Override
    public Optional<JsonCodec> create() {
        return enabled ? Optional.of(createCodec()) : Optional.empty();
    }

    private static JsonCodec createCodec() {
        ObjectMapper objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new JavaTimeModule())
                .addMixIn(TokenDto.class, TokenDtoMixin.class)
                .addMixIn(Manifest.class, ManifestMixin.class);
        return new JacksonJsonCodec(objectMapper);
    }

}
