package com.gihtub.alexeylapin.conreg.client.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihtub.alexeylapin.conreg.json.jackson.JacksonJsonCodec;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class FileAuthenticationHolderTest {

    @Test
    void name() {
        FileAuthenticationHolder holder = new WellKnownFileAuthHolders()
                .create(new JacksonJsonCodec(new ObjectMapper()))
                .orElseThrow();
        Optional<String> forRegistry = holder.getForRegistry("ghcr.io");
        System.out.println(forRegistry);
    }

}