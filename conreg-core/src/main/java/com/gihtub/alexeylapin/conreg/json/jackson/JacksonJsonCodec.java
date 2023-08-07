package com.gihtub.alexeylapin.conreg.json.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import lombok.SneakyThrows;

import java.util.List;

public class JacksonJsonCodec implements JsonCodec {

    private final ObjectMapper objectMapper;

    public JacksonJsonCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public <T> String encode(T object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    @Override
    public <T> T decode(String json, Class<T> aClass) {
        return objectMapper.readValue(json, aClass);
    }

    @SneakyThrows
    @Override
    public <T> List<T> decodeList(String json, Class<T> aClass) {
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, aClass);
        return objectMapper.readValue(json, javaType);
    }

}
