package com.gihtub.alexeylapin.conreg.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihtub.alexeylapin.conreg.json.Json;
import lombok.SneakyThrows;

public class JacksonJson implements Json {

    private final ObjectMapper objectMapper;

    public JacksonJson(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public <T> String render(T object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    @Override
    public <T> T parse(String json, Class<T> aClass) {
        return objectMapper.readValue(json, aClass);
    }

}
