package com.gihtub.alexeylapin.conreg.json.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihtub.alexeylapin.conreg.client.http.dto.TokenDto;
import com.gihtub.alexeylapin.conreg.facade.factory.JsonCodecFactory;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;

import java.util.Optional;

public class JacksonJsonCodecFactory implements JsonCodecFactory {

    private final boolean enabled;

    public JacksonJsonCodecFactory() {
        boolean isObjectMappedLoadable;
        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            isObjectMappedLoadable = true;
        } catch (ClassNotFoundException e) {
            isObjectMappedLoadable = false;
        }
        this.enabled = isObjectMappedLoadable;
    }

    @Override
    public Optional<JsonCodec> create() {
        return enabled ? Optional.of(createCodec()) : Optional.empty();
    }

    private static JsonCodec createCodec() {
        ObjectMapper objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addMixIn(TokenDto.class, TokenDtoMixin.class);
        return new JacksonJsonCodec(objectMapper);
    }

}
