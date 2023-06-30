package com.github.alexeylapin.conreg.http;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.Registry;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.client.http.dto.TokenDto;
import com.gihtub.alexeylapin.conreg.image.Blob;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.regex.Matcher;

public class JdkApiClient implements ApiClient {

    private final RegistryResolver registryResolver;
    private final HttpClient httpClient;
    private final JsonCodec jsonCodec;
    private final AuthenticationProvider authenticationProvider;

    public JdkApiClient(RegistryResolver registryResolver,
                        HttpClient httpClient,
                        JsonCodec jsonCodec, AuthenticationProvider authenticationProvider) {
        this.registryResolver = registryResolver;
        this.httpClient = httpClient;
        this.jsonCodec = jsonCodec;
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public String authenticate(String registry, String challenge) {
        Matcher matcher = AUTH_CHALLENGE_PATTERN.matcher(challenge);
        matcher.matches();
        String uri = String.format("%s?service=%s&scope=%s", matcher.group(1), matcher.group(2), matcher.group(3));
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .GET();
            authenticationProvider.getForRegistry(Registry.of(registry)).ifPresent(auth -> {
                requestBuilder.setHeader("authorization", auth);
            });
            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            TokenDto tokenDto = jsonCodec.decode(response.body(), TokenDto.class);
            return "Bearer " + tokenDto.getToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ManifestDto getManifest(Reference reference) {
        String uri = String.format(URL_MANIFEST,
                resolveRegistry(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName(),
                reference.getTagOrDigest());
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .header("Accept", "application/vnd.docker.distribution.manifest.v2+json")
                .GET();
        HttpResponse<String> response = withAuth(reference.getRegistry(),
                requestBuilder,
                HttpResponse.BodyHandlers.ofString());
        return jsonCodec.decode(response.body(), ManifestDto.class);
    }

    @Override
    public InputStream getBlob(Reference reference, String digest) {
        String uri = String.format(URL_BLOB,
                resolveRegistry(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName(),
                digest);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .GET();
        HttpResponse<InputStream> response = withAuth(reference.getRegistry(),
                requestBuilder,
                HttpResponse.BodyHandlers.ofInputStream());
        return response.body();
    }

    @Override
    public void putBlob(Reference reference, String digest, Blob blob) {
        String uri = String.format(URL_BLOB,
                resolveRegistry(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName(),
                digest);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .header("content-type", "application/octet-stream")
                .PUT(HttpRequest.BodyPublishers.ofInputStream(blob.getContent()));
        HttpResponse<Void> response = withAuth(reference.getRegistry(),
                requestBuilder,
                HttpResponse.BodyHandlers.discarding());
        response.statusCode();
    }

    public boolean isBlobExists(Reference reference, String digest) {
        String uri = String.format(URL_BLOB,
                resolveRegistry(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName(),
                digest);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .method("HEAD", HttpRequest.BodyPublishers.noBody());
        HttpResponse<Void> response = withAuth(reference.getRegistry(),
                requestBuilder,
                HttpResponse.BodyHandlers.discarding());
        return true;
    }

    public String startPush(Reference reference) {
        String uri = String.format(URL_BLOB_UPLOAD,
                resolveRegistry(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName());
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .header("content-type", "application/vnd.docker.image.rootfs.diff.tar.gzip")
                .POST(HttpRequest.BodyPublishers.noBody());
        HttpResponse<Void> response = withAuth(reference.getRegistry(),
                requestBuilder,
                HttpResponse.BodyHandlers.discarding());
        return response.headers().firstValue("location").orElseThrow();
    }

    @SneakyThrows
    private <T> HttpResponse<T> withAuth(String registry,
                                         HttpRequest.Builder requestBuilder,
                                         HttpResponse.BodyHandler<T> bodyHandler) {
        // try existing token
        HttpRequest.Builder builder = requestBuilder.copy();
        HttpResponse<T> response = httpClient.send(builder.build(), bodyHandler);
        if (response.statusCode() == 401) {
            Optional<String> challengeHeaderOptional = response.headers().firstValue("www-authenticate");
            if (challengeHeaderOptional.isPresent()) {
                String challenge = challengeHeaderOptional.get();
                String authorization = authenticate(registry, challenge);
                builder.setHeader("authorization", authorization);
                response = httpClient.send(builder.build(), bodyHandler);
            }
        }
        if (response.statusCode() >= 300) {
            throw new RuntimeException("unexpected status code: " + response.statusCode());
        }
        return response;
    }

    @SneakyThrows
    private static URI createURI(String uri) {
        return new URI(uri);
    }

    private String resolveRegistry(String registry) {
        return registryResolver.resolve(registry);
    }

}
