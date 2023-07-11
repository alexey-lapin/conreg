package com.gihtub.alexeylapin.conreg.json.gson;

import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.google.gson.Gson;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GsonJsonCodec implements JsonCodec {

    @NonNull
    private final Gson gson;

    @Override
    public <T> String encode(T object) {
        return gson.toJson(object);
    }

    @Override
    public <T> T decode(String json, Class<T> aClass) {
        return gson.fromJson(json, aClass);
    }

}
