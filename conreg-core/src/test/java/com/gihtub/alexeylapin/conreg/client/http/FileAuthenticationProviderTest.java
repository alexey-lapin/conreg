package com.gihtub.alexeylapin.conreg.client.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.FileAuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.Registry;
import com.gihtub.alexeylapin.conreg.json.jackson.JacksonJsonCodec;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class FileAuthenticationProviderTest {

    @Test
    void name() {
        AuthenticationProvider holder = new WellKnownFileAuthenticationProviderFactory()
                .create(new JacksonJsonCodec(new ObjectMapper()))
                .orElseThrow();
        Optional<String> forRegistry = holder.getForRegistry(Registry.of("ghcr.io"));
        System.out.println(forRegistry);
    }

}