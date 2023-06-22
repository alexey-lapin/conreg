package com.gihtub.alexeylapin.conreg.json;

public interface JsonCodec {

    <T> String encode(T object);

    <T> T decode(String json, Class<T> aClass);

}
