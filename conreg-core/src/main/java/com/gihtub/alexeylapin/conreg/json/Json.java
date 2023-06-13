package com.gihtub.alexeylapin.conreg.json;

public interface Json {

    <T> String render(T object);

    <T> T parse(String json);

}
