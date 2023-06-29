package com.github.alexeylapin.conreg.http;

import com.gihtub.alexeylapin.conreg.client.http.ApiClient;
import com.gihtub.alexeylapin.conreg.client.http.Authenticator;
import com.gihtub.alexeylapin.conreg.client.http.RegistryResolver;
import com.gihtub.alexeylapin.conreg.client.http.dto.DockerAuthDto;
import com.gihtub.alexeylapin.conreg.client.http.dto.ManifestDto;
import com.gihtub.alexeylapin.conreg.image.Blob;
import com.gihtub.alexeylapin.conreg.image.Reference;
import com.gihtub.alexeylapin.conreg.json.JsonCodec;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;

public class JdkApiClient implements ApiClient {

    private final RegistryResolver registryResolver;
    private final HttpClient httpClient;
    private final Authenticator authenticator;
    private final JsonCodec jsonCodec;

    public JdkApiClient(RegistryResolver registryResolver,
                        HttpClient httpClient,
                        Authenticator authenticator,
                        JsonCodec jsonCodec) {
        this.registryResolver = registryResolver;
        this.httpClient = httpClient;
        this.authenticator = authenticator;
        this.jsonCodec = jsonCodec;
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
//            getForRegistry(registry).ifPresent(auth -> {
//                requestBuilder.setHeader("authorization", auth);
//            });
            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            DockerAuthDto dockerAuthDto = jsonCodec.decode(response.body(), DockerAuthDto.class);
            return "Bearer " + dockerAuthDto.getToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ManifestDto getManifest(Reference reference) {
        try {
            String uri = String.format(URL_MANIFEST,
                    resolveRegistry(reference.getRegistry()),
                    reference.getNamespace(),
                    reference.getName(),
                    reference.getTagOrDigest());
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Accept", "application/vnd.docker.distribution.manifest.v2+json")
                    .GET();
            HttpResponse<String> response = withAuth(reference.getRegistry(), requestBuilder, HttpResponse.BodyHandlers.ofString());
            return jsonCodec.decode(response.body(), ManifestDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getBlob(Reference reference, String digest) {
        try {
            String uri = String.format(URL_BLOB,
                    resolveRegistry(reference.getRegistry()),
                    reference.getNamespace(),
                    reference.getName(),
                    digest);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .GET();
            HttpResponse<InputStream> response = withAuth(reference.getRegistry(), requestBuilder, HttpResponse.BodyHandlers.ofInputStream());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putBlob(Reference reference, String digest, Blob blob) {
        try {
            String uri = String.format(URL_BLOB,
                    resolveRegistry(reference.getRegistry()),
                    reference.getNamespace(),
                    reference.getName(),
                    digest);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("content-type", "application/octet-stream")
                    .PUT(HttpRequest.BodyPublishers.ofInputStream(blob.getContent()));
            HttpResponse<Void> response = withAuth(reference.getRegistry(), requestBuilder, HttpResponse.BodyHandlers.discarding());
            response.statusCode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isBlobExists(Reference reference, String digest) {
        try {
            String uri = String.format(URL_BLOB,
                    resolveRegistry(reference.getRegistry()),
                    reference.getNamespace(),
                    reference.getName(),
                    digest);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody());
            HttpResponse<Void> response = withAuth(reference.getRegistry(), requestBuilder, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() < 400;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String startPush(Reference reference) {
        try {
            String uri = String.format(URL_BLOB_UPLOAD,
                    resolveRegistry(reference.getRegistry()),
                    reference.getNamespace(),
                    reference.getName());
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("content-type", "application/vnd.docker.image.rootfs.diff.tar.gzip")
                    .POST(HttpRequest.BodyPublishers.noBody());
            HttpResponse<Void> response = withAuth(reference.getRegistry(), requestBuilder, HttpResponse.BodyHandlers.discarding());
            return response.headers().firstValue("location").orElseThrow();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private <T> HttpResponse<T> withAuth(String registry,
                                         HttpRequest.Builder requestBuilder,
                                         HttpResponse.BodyHandler<T> bodyHandler) {
        // try existing token
        HttpResponse<T> response = httpClient.send(requestBuilder.build(), bodyHandler);
        if (response.statusCode() == 401) {
            response.headers().firstValue("www-authenticate").ifPresent(value -> {
                String authorization = authenticate(registry, value);
                requestBuilder.setHeader("authorization", authorization);
            });
            response = httpClient.send(requestBuilder.build(), bodyHandler);
        }
        return response;
    }

    private String resolveRegistry(String registry) {
        return registryResolver.resolve(registry);
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
