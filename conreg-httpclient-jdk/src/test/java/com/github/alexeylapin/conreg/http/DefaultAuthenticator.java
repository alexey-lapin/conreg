package com.github.alexeylapin.conreg.http;

import com.gihtub.alexeylapin.conreg.client.http.AuthenticationHolder;
import com.gihtub.alexeylapin.conreg.client.http.Authenticator;
import com.gihtub.alexeylapin.conreg.client.http.dto.DockerAuthDto;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultAuthenticator implements Authenticator {

    private final HttpClient httpClient;
    private final JsonCodec jsonCodec;
    private final AuthenticationHolder authenticationHolder;

    public DefaultAuthenticator(HttpClient httpClient, JsonCodec jsonCodec, AuthenticationHolder authenticationHolder) {
        this.httpClient = httpClient;
        this.jsonCodec = jsonCodec;
        this.authenticationHolder = authenticationHolder;
    }

    @Override
    public Optional<String> getForRegistry(String registry) {
        return authenticationHolder.getForRegistry(registry).map(value -> "Basic " + value);
    }

    @Override
    public String authenticate(String registry, String context) {
        String[] parts = context.split(",");
        Map<String, String> map = new HashMap<>();
        for (String part : parts) {
            String[] pairs = part.split("=");
            map.put(pairs[0], pairs[1]);
        }
        String realm = unquote(map.get("Bearer realm"));
        String service = unquote(map.get("service"));
        String scope = unquote(map.get("scope"));
        String uri = String.format("%s?service=%s&scope=%s", realm, service, scope);
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .GET();
            getForRegistry(registry).ifPresent(auth -> {
                requestBuilder.setHeader("authorization", auth);
            });
            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            DockerAuthDto dockerAuthDto = jsonCodec.decode(response.body(), DockerAuthDto.class);
            return "Bearer " + dockerAuthDto.getToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String unquote(String string) {
        if (string.startsWith("\"")) {
            string = string.substring(1);
        }
        if (string.endsWith("\"")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

}
