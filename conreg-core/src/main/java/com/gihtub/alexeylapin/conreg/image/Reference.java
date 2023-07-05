package com.gihtub.alexeylapin.conreg.image;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [REGISTRY/][NAMESPACE/]NAME[:TAG|@DIGEST]
 */
public interface Reference {

    String getRegistry();

    String getNamespace();

    String getName();

    Optional<String> getTag();

    Optional<String> getDigest();

    String getTagOrDigest();

    Reference withRegistry(String registry);

    Reference withNamespace(String namespace);

    Reference withName(String name);

    Reference withTag(String tag);

    Reference withDigest(String digest);

    static Reference of(String s) {
        return DefaultReference.parse(s);
    }

    @Getter
    @With
    class DefaultReference implements Reference {

        // @formatter:off
        public static final Pattern PATTERN = Pattern.compile(
                "^" +                                           // Start of the line
                "(?:(" +                                        // Start of non-capturing group for registry
                    "(?:[a-z0-9]+(?:[._-][a-z0-9]+)*\\.)+" +    // Domain part of the registry (e.g., registry.example.com)
                    "[a-z]{2,6}" +                              // TLD part of the registry
                    "(?::[0-9]{1,5})?" +                        // Optional port number part of the registry
                    "|localhost" +                              // OR "localhost" (to handle cases like "localhost:5000")
                    "(?::[0-9]{1,5})?" +                        // Optional port number for localhost
                ")/)?" +                                        // End of non-capturing group for registry
                "(?:(.+)/)?" +                                  // Non-capturing group for namespace (e.g., "myproject/")
                "([^/@:]+)" +                                   // Repo/Image name (e.g., "myimage")
                "(?::(.+))?" +                                  // Optional non-capturing group for tag (e.g., ":1.0.0")
                "(?:@(.+))?" +                                  // Optional non-capturing group for digest (e.g., "@sha256:b2f1cfa431b0")
                "$"                                             // End of the line
        );
        // @formatter:on

        public static final String DEFAULT_REGISTRY = "docker.io";
        public static final String DEFAULT_NAMESPACE = "library";
        public static final String DEFAULT_TAG = "latest";

        private final String registry;
        private final String namespace;
        private final String name;
        private final String tag;
        private final String digest;

        @Builder
        public DefaultReference(String registry, String namespace, String name, String tag, String digest) {
            this.registry = Objects.requireNonNull(registry, "registry must not be null");
            this.namespace = Objects.requireNonNull(namespace, "namespace must not be null");
            this.name = Objects.requireNonNull(name, "name must not be null");
            if (tag == null && digest == null) {
                throw new NullPointerException("tag or digest must not be null");
            }
            this.tag = tag;
            this.digest = digest;
        }

        public static DefaultReference parse(String s) {
            Matcher matcher = PATTERN.matcher(s);
            if (matcher.find()) {
                String registry = matcher.group(1);
                String namespace = matcher.group(2);
                String name = matcher.group(3);
                String tag = matcher.group(4);
                String digest = matcher.group(5);
                return DefaultReference.builder()
                        .registry(registry == null ? DEFAULT_REGISTRY : registry)
                        .namespace(namespace == null ? DEFAULT_NAMESPACE : namespace)
                        .name(name)
                        .tag(tag == null ? DEFAULT_TAG : tag)
                        .digest(digest)
                        .build();
            }
            throw new IllegalArgumentException("Bad image reference");
        }

        @Override
        public Optional<String> getTag() {
            return digest == null ? Optional.of(tag) : Optional.empty();
        }

        @Override
        public Optional<String> getDigest() {
            return Optional.ofNullable(digest);
        }

        @Override
        public String getTagOrDigest() {
            return digest == null ? tag : digest;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(registry).append("/");
            sb.append(namespace).append("/");
            sb.append(name);
            if (digest == null) {
                sb.append(":").append(tag);
            } else {
                sb.append("@").append(digest);
            }
            return sb.toString();
        }
    }

}
