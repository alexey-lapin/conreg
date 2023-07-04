package com.github.alexeylapin.conreg.http;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.auth.Action;
import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.Registry;
import com.gihtub.alexeylapin.conreg.client.http.auth.Scope;
import com.gihtub.alexeylapin.conreg.client.http.auth.TokenKey;
import com.gihtub.alexeylapin.conreg.client.http.auth.TokenStore;
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
    private final TokenStore tokenStore;

    public JdkApiClient(RegistryResolver registryResolver,
                        HttpClient httpClient,
                        JsonCodec jsonCodec, AuthenticationProvider authenticationProvider,
                        TokenStore tokenStore) {
        this.registryResolver = registryResolver;
        this.httpClient = httpClient;
        this.jsonCodec = jsonCodec;
        this.authenticationProvider = authenticationProvider;
        this.tokenStore = tokenStore;
    }

    @SneakyThrows
    @Override
    public Optional<TokenDto> authenticate(String registry, String challenge) {
        Matcher challengeMatcher = AUTH_CHALLENGE_PATTERN.matcher(challenge);
        if (!challengeMatcher.matches()) {
            return Optional.empty();
        }
        Matcher scopeMatcher = SCOPE_PATTERN.matcher(challengeMatcher.group(3));
        if (!scopeMatcher.matches()) {
            return Optional.empty();
        }

        String uri = String.format("%s?service=%s&scope=%s",
                challengeMatcher.group(1), challengeMatcher.group(2), challengeMatcher.group(3));
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .GET();
        authenticationProvider.getForRegistry(Registry.of(registry)).ifPresent(auth -> {
            requestBuilder.setHeader("authorization", auth);
        });
        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        validateResponse(response);

        TokenDto tokenDto = jsonCodec.decode(response.body(), TokenDto.class);

//        for (String action : scopeMatcher.group(3).split(",")) {
//            Scope scope = Scope.of(scopeMatcher.group(1), scopeMatcher.group(2), Action.of(action));
//            TokenKey tokenKey = TokenKey.of(challengeMatcher.group(2), scope);
//        }

        return Optional.of(tokenDto);
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
        HttpResponse<String> response = withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.ofString(),
                Action.PULL);
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
        HttpResponse<InputStream> response = withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.ofInputStream(),
                Action.PULL);
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
        HttpResponse<Void> response = withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.discarding(),
                Action.PUSH);
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
        HttpResponse<Void> response = withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.discarding(),
                Action.PULL);
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
        HttpResponse<Void> response = withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.discarding(),
                Action.PUSH);
        return response.headers().firstValue("location").orElseThrow();
    }

    @SneakyThrows
    private <T> HttpResponse<T> withAuth(Reference reference,
                                         HttpRequest.Builder requestBuilder,
                                         HttpResponse.BodyHandler<T> bodyHandler,
                                         Action action) {
        HttpRequest.Builder builder = requestBuilder.copy();
        TokenKey key = TokenKey.of(resolveRegistry(reference.getRegistry()), Scope.repository(reference, action));
        tokenStore.retrieve(key).ifPresent(auth -> {
            builder.setHeader("authorization", auth.getAuth());
        });
        HttpResponse<T> response = httpClient.send(builder.build(), bodyHandler);
        if (response.statusCode() == 401) {
            Optional<String> challengeHeaderOptional = response.headers().firstValue("www-authenticate");
            if (challengeHeaderOptional.isPresent()) {
                String challenge = challengeHeaderOptional.get();
                Optional<TokenDto> tokenOptional = authenticate(reference.getRegistry(), challenge);
                if (tokenOptional.isPresent()) {
                    TokenDto tokenDto = tokenOptional.get();
//                    Auth auth = tokenStore.store(Registry.of(registry), null);
//                    builder.setHeader("authorization", auth.getAuth());
                    builder.setHeader("authorization", "Bearer " + tokenDto.getToken());
                    response = httpClient.send(builder.build(), bodyHandler);
                }
            }
        }
        validateResponse(response);
        return response;
    }

    @SneakyThrows
    private static URI createURI(String uri) {
        return new URI(uri);
    }

    private String resolveRegistry(String registry) {
        return registryResolver.resolve(registry);
    }

    private static void validateResponse(HttpResponse<?> response) {
        if (response.statusCode() >= 300) {
            throw new RuntimeException("unexpected status code: " + response.statusCode());
        }
    }

}
