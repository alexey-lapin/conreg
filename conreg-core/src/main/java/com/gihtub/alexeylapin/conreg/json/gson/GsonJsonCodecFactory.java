package com.gihtub.alexeylapin.conreg.json.gson;

import com.gihtub.alexeylapin.conreg.client.http.dto.TokenDto;
import com.gihtub.alexeylapin.conreg.facade.factory.JsonCodecFactory;
import com.gihtub.alexeylapin.conreg.image.Manifest;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Optional;

public class GsonJsonCodecFactory implements JsonCodecFactory {

    private final boolean enabled;

    public GsonJsonCodecFactory() {
        boolean isGsonLoadable;
        try {
            Class.forName("com.google.gson.Gson");
            isGsonLoadable = true;
        } catch (ClassNotFoundException e) {
            isGsonLoadable = false;
        }
        this.enabled = isGsonLoadable;
    }

    @Override
    public Optional<JsonCodec> create() {
        return enabled ? Optional.of(createCodec()) : Optional.empty();
    }

    private static JsonCodec createCodec() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TokenDto.class, new TokenDtoTypeAdapter())
                .registerTypeAdapter(Manifest.class, new ManifestTypeAdapter())
                .create();
        return new GsonJsonCodec(gson);
    }

}
