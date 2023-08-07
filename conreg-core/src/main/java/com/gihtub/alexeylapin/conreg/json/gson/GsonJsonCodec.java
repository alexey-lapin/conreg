package com.gihtub.alexeylapin.conreg.json.gson;

import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.List;

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

    @Override
    public <T> List<T> decodeList(String json, Class<T> aClass) {
        Type type = TypeToken.getParameterized(List.class, aClass).getType();
        return gson.fromJson(json, type);
    }

}
