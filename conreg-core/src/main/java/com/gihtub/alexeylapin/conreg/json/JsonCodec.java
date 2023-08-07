package com.gihtub.alexeylapin.conreg.json;

import java.util.List;

public interface JsonCodec {

    <T> String encode(T object);

    <T> T decode(String json, Class<T> aClass);

    <T> List<T> decodeList(String json, Class<T> aClass);

}
