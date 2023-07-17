package com.github.alexeylapin.conreg.http;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.auth.Action;
import com.gihtub.alexeylapin.conreg.client.http.auth.Auth;
import com.gihtub.alexeylapin.conreg.client.http.auth.AuthenticationProvider;
import com.gihtub.alexeylapin.conreg.client.http.auth.Registry;
import com.gihtub.alexeylapin.conreg.client.http.auth.Scope;
import com.gihtub.alexeylapin.conreg.client.http.auth.Token;
import com.gihtub.alexeylapin.conreg.client.http.auth.TokenKey;
import com.gihtub.alexeylapin.conreg.client.http.auth.TokenStore;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDescriptor;
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
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;

public class JdkApiClient implements ApiClient {

    private static final Predicate<HttpResponse<?>> RESPONSE_PREDICATE_SUCCESS =
            response -> response.statusCode() < 300;

    private static final Predicate<HttpResponse<?>> RESPONSE_PREDICATE_SUCCESS_OR_404 =
            RESPONSE_PREDICATE_SUCCESS.or(response -> response.statusCode() == 404);

    private final HttpClient httpClient;
    private final RegistryResolver registryResolver;
    private final JsonCodec jsonCodec;
    private final AuthenticationProvider authenticationProvider;
    private final TokenStore tokenStore;

    public JdkApiClient(HttpClient httpClient,
                        RegistryResolver registryResolver,
                        JsonCodec jsonCodec,
                        AuthenticationProvider authenticationProvider,
                        TokenStore tokenStore) {
        this.httpClient = httpClient;
        this.registryResolver = registryResolver;
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
        validateResponse(RESPONSE_PREDICATE_SUCCESS, response);

        TokenDto tokenDto = jsonCodec.decode(response.body(), TokenDto.class);

        for (String action : scopeMatcher.group(3).split(",")) {
            Scope scope = Scope.of(scopeMatcher.group(1), scopeMatcher.group(2), Action.of(action));
            TokenKey tokenKey = TokenKey.of(challengeMatcher.group(2), scope);
            tokenStore.store(tokenKey, Token.builder()
                    .key(tokenKey)
                    .token(tokenDto.getToken())
                    .accessToken(tokenDto.getAccessToken().orElse(null))
                    .expiresIn(tokenDto.getExpiresIn().orElse(null))
                    .issuedAt(tokenDto.getIssuedAt().orElse(ZonedDateTime.now()))
                    .build());
        }

        return Optional.of(tokenDto);
    }

    @Override
    public ManifestDescriptor getManifest(Reference reference) {
        String uri = String.format(URL_MANIFEST,
                resolveRegistryServiceUri(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName(),
                reference.getTagOrDigest());
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .header("accept", "application/vnd.docker.distribution.manifest.v2+json")
                .GET();
        HttpResponse<String> response = withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.ofString(),
                Action.PULL,
                RESPONSE_PREDICATE_SUCCESS);
        return jsonCodec.decode(response.body(), ManifestDescriptor.class);
    }

    @Override
    public void putManifest(Reference reference, ManifestDescriptor manifestDescriptor) {
        String uri = String.format(URL_MANIFEST,
                resolveRegistryServiceUri(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName(),
                reference.getTagOrDigest());
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .header("content-type", manifestDescriptor.getMediaType())
                .PUT(HttpRequest.BodyPublishers.ofString(jsonCodec.encode(manifestDescriptor)));
        withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.ofString(),
                Action.PUSH,
                RESPONSE_PREDICATE_SUCCESS);
    }

    @Override
    public void deleteManifest(Reference reference) {
        String uri = String.format(URL_MANIFEST,
                resolveRegistryServiceUri(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName(),
                reference.getTagOrDigest());
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .DELETE();
        withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.ofString(),
                Action.PUSH,
                RESPONSE_PREDICATE_SUCCESS);
    }

    @Override
    public URI startPush(Reference reference) {
        String uri = String.format(URL_BLOB_UPLOAD,
                resolveRegistryServiceUri(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName());
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .POST(HttpRequest.BodyPublishers.noBody());
        HttpResponse<String> response = withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.ofString(),
                Action.PUSH,
                RESPONSE_PREDICATE_SUCCESS);
        String location = response.headers().firstValue("location")
                .orElseThrow();
        if (location.startsWith(SCHEMA_HTTP)) {
            return createURI(location);
        }
        return createURI(resolveRegistryServiceUri(reference.getRegistry()) + location);
    }

    @Override
    public void cancelPush(Reference reference, URI uri) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE();
        withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.ofString(),
                Action.PUSH,
                RESPONSE_PREDICATE_SUCCESS);
    }

    @Override
    public boolean isBlobExists(Reference reference, String digest) {
        String uri = String.format(URL_BLOB,
                resolveRegistryServiceUri(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName(),
                digest);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .method("HEAD", HttpRequest.BodyPublishers.noBody());
        HttpResponse<String> response = withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.ofString(),
                Action.PULL,
                RESPONSE_PREDICATE_SUCCESS_OR_404);
        return response.statusCode() == 200;
    }

    @Override
    public InputStream getBlob(Reference reference, String digest) {
        String uri = String.format(URL_BLOB,
                resolveRegistryServiceUri(reference.getRegistry()),
                reference.getNamespace(),
                reference.getName(),
                digest);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uri))
                .GET();
        HttpResponse<InputStream> response = withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.ofInputStream(),
                Action.PULL,
                RESPONSE_PREDICATE_SUCCESS);
        return response.body();
    }

    @Override
    public void putBlob(Reference reference, URI uploadUri, String digest, Blob blob) {
        StringBuilder queryBuilder = new StringBuilder();
        if (uploadUri.getQuery() == null) {
            queryBuilder.append("digest=").append(digest);
        } else {
            queryBuilder.append(uploadUri.getQuery()).append("&digest=").append(digest);
        }
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createURI(uploadUri, queryBuilder.toString()))
                .header("content-type", "application/octet-stream")
                .PUT(HttpRequest.BodyPublishers.ofInputStream(blob.getContent().unchecked()));
        withAuth(reference,
                requestBuilder,
                HttpResponse.BodyHandlers.ofString(),
                Action.PUSH,
                RESPONSE_PREDICATE_SUCCESS);
    }

    @SneakyThrows
    private <T> HttpResponse<T> withAuth(Reference reference,
                                         HttpRequest.Builder requestBuilder,
                                         HttpResponse.BodyHandler<T> bodyHandler,
                                         Action action,
                                         Predicate<HttpResponse<?>> responsePredicate) {
        HttpRequest.Builder builder = requestBuilder.copy();
        String serviceName = registryResolver.resolve(reference.getRegistry()).getServiceName();
        TokenKey key = TokenKey.of(serviceName, Scope.repository(reference, action));
        tokenStore.retrieve(key).ifPresent(token -> {
            builder.setHeader("authorization", Auth.bearer(token.getToken()).getValue());
        });
        HttpResponse<T> response = httpClient.send(builder.build(), bodyHandler);
        if (response.statusCode() == 401) {
            Optional<String> challengeHeaderOptional = response.headers().firstValue("www-authenticate");
            if (challengeHeaderOptional.isPresent()) {
                String challenge = challengeHeaderOptional.get();
                Optional<TokenDto> tokenOptional = authenticate(reference.getRegistry(), challenge);
                if (tokenOptional.isPresent()) {
                    TokenDto tokenDto = tokenOptional.get();
                    builder.setHeader("authorization", Auth.bearer(tokenDto.getToken()).getValue());
                    response = httpClient.send(builder.build(), bodyHandler);
                }
            }
        }
        validateResponse(responsePredicate, response);
        return response;
    }

    @SneakyThrows
    private static URI createURI(String uri) {
        return new URI(uri);
    }

    @SneakyThrows
    private static URI createURI(URI uri, String query) {
        return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
    }

    private URI resolveRegistryServiceUri(String registry) {
        return registryResolver.resolve(registry).getServiceUri();
    }

    private static void validateResponse(Predicate<HttpResponse<?>> responsePredicate, HttpResponse<?> response) {
        if (!responsePredicate.test(response)) {
            Object body = response.body();
            String message = "unexpected status code: " + response.statusCode();
            if (body instanceof String) {
                message += ", body: " + body;
            }
            throw new RuntimeException(message);
        }
    }

}
