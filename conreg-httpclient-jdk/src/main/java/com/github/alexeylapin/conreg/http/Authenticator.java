package com.github.alexeylapin.conreg.http;

import com.gihtub.alexeylapin.conreg.client.http.dto.DockerAuthDto;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Authenticator {

    private final HttpClient httpClient;
    private final JsonCodec jsonCodec;

    public Authenticator(HttpClient httpClient, JsonCodec jsonCodec) {
        this.httpClient = httpClient;
        this.jsonCodec = jsonCodec;
    }

    public String authenticate(String context) {
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
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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
