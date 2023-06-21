package com.gihtub.alexeylapin.conreg.image;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReferenceTest {

    public static final String REGISTRY_DOCKER_IO = "docker.io";
    public static final String NAMESPACE_LIBRARY = "library";
    public static final String NAME_ALPINE = "alpine";
    public static final String TAG_LATEST = "latest";

    @Test
    void should_parse_when_referenceContainsOnlyName() {
        Reference reference = Reference.DefaultReference.parse("alpine");

        assertThat(reference.getRegistry()).isEqualTo(REGISTRY_DOCKER_IO);
        assertThat(reference.getNamespace()).isEqualTo(NAMESPACE_LIBRARY);
        assertThat(reference.getName()).isEqualTo(NAME_ALPINE);
        assertThat(reference.getTag()).contains(TAG_LATEST);
        assertThat(reference.getDigest()).isEmpty();
        assertThat(reference.getTagOrDigest()).isEqualTo(TAG_LATEST);
        assertThat(reference.toString()).isEqualTo("docker.io/library/alpine:latest");
    }

    @Test
    void should_parse_when_referenceContainsOneLevelNamespaceAndName() {
        Reference reference = Reference.DefaultReference.parse("lib/alpine");

        assertThat(reference.getRegistry()).isEqualTo(REGISTRY_DOCKER_IO);
        assertThat(reference.getNamespace()).isEqualTo("lib");
        assertThat(reference.getName()).isEqualTo(NAME_ALPINE);
        assertThat(reference.getTag()).contains(TAG_LATEST);
        assertThat(reference.getDigest()).isEmpty();
        assertThat(reference.getTagOrDigest()).isEqualTo(TAG_LATEST);
        assertThat(reference.toString()).isEqualTo("docker.io/lib/alpine:latest");
    }

    @Test
    void should_parse_when_referenceContainsOneLevelNamespaceNameAndTag() {
        Reference reference = Reference.DefaultReference.parse("lib/alpine:0.0.1");

        assertThat(reference.getRegistry()).isEqualTo(REGISTRY_DOCKER_IO);
        assertThat(reference.getNamespace()).isEqualTo("lib");
        assertThat(reference.getName()).isEqualTo(NAME_ALPINE);
        assertThat(reference.getTag()).contains("0.0.1");
        assertThat(reference.getDigest()).isEmpty();
        assertThat(reference.getTagOrDigest()).isEqualTo("0.0.1");
        assertThat(reference.toString()).isEqualTo("docker.io/lib/alpine:0.0.1");
    }

    @Test
    void should_parse_when_referenceContainsTwoLevelNamespaceNameAndTag() {
        Reference reference = Reference.DefaultReference.parse("project/subproject/alpine:0.0.1");

        assertThat(reference.getRegistry()).isEqualTo(REGISTRY_DOCKER_IO);
        assertThat(reference.getNamespace()).isEqualTo("project/subproject");
        assertThat(reference.getName()).isEqualTo(NAME_ALPINE);
        assertThat(reference.getTag()).contains("0.0.1");
        assertThat(reference.getDigest()).isEmpty();
        assertThat(reference.getTagOrDigest()).isEqualTo("0.0.1");
        assertThat(reference.toString()).isEqualTo("docker.io/project/subproject/alpine:0.0.1");
    }

    @Test
    void should_parse_when_referenceContainsThreeLevelNamespaceNameAndTag() {
        Reference reference = Reference.DefaultReference.parse("project/subproject/subsubproject/alpine:0.0.1");

        assertThat(reference.getRegistry()).isEqualTo(REGISTRY_DOCKER_IO);
        assertThat(reference.getNamespace()).isEqualTo("project/subproject/subsubproject");
        assertThat(reference.getName()).isEqualTo(NAME_ALPINE);
        assertThat(reference.getTag()).contains("0.0.1");
        assertThat(reference.getDigest()).isEmpty();
        assertThat(reference.getTagOrDigest()).isEqualTo("0.0.1");
        assertThat(reference.toString()).isEqualTo("docker.io/project/subproject/subsubproject/alpine:0.0.1");
    }

    @Test
    void should_parse_when_referenceContainsNameAndDigest() {
        Reference reference = Reference.DefaultReference.parse("lib/alpine@sha256:26c68657ccce2cb0a31b330cb0be2b5e108d467f641c62e13ab40cbec258c68d");

        assertThat(reference.getRegistry()).isEqualTo(REGISTRY_DOCKER_IO);
        assertThat(reference.getNamespace()).isEqualTo("lib");
        assertThat(reference.getName()).isEqualTo(NAME_ALPINE);
        assertThat(reference.getTag()).isEmpty();
        assertThat(reference.getDigest()).contains("sha256:26c68657ccce2cb0a31b330cb0be2b5e108d467f641c62e13ab40cbec258c68d");
        assertThat(reference.getTagOrDigest()).isEqualTo("sha256:26c68657ccce2cb0a31b330cb0be2b5e108d467f641c62e13ab40cbec258c68d");
        assertThat(reference.toString()).isEqualTo("docker.io/lib/alpine@sha256:26c68657ccce2cb0a31b330cb0be2b5e108d467f641c62e13ab40cbec258c68d");
    }

    @Test
    void should_parse_when_referenceContainsRegistryOneLevelNamespaceNameAndTag() {
        Reference reference = Reference.DefaultReference.parse("ghcr.io/lib/alpine:0.0.1");

        assertThat(reference.getRegistry()).isEqualTo("ghcr.io");
        assertThat(reference.getNamespace()).isEqualTo("lib");
        assertThat(reference.getName()).isEqualTo(NAME_ALPINE);
        assertThat(reference.getTag()).contains("0.0.1");
        assertThat(reference.getDigest()).isEmpty();
        assertThat(reference.getTagOrDigest()).isEqualTo("0.0.1");
        assertThat(reference.toString()).isEqualTo("ghcr.io/lib/alpine:0.0.1");
    }

    @Test
    void should_parse_when_referenceContainsRegistryNameAndTag() {
        Reference reference = Reference.DefaultReference.parse("ghcr.io/alpine:0.0.1");

        assertThat(reference.getRegistry()).isEqualTo("ghcr.io");
        assertThat(reference.getNamespace()).isEqualTo(NAMESPACE_LIBRARY);
        assertThat(reference.getName()).isEqualTo(NAME_ALPINE);
        assertThat(reference.getTag()).contains("0.0.1");
        assertThat(reference.getDigest()).isEmpty();
        assertThat(reference.getTagOrDigest()).isEqualTo("0.0.1");
        assertThat(reference.toString()).isEqualTo("ghcr.io/library/alpine:0.0.1");
    }

    @Test
    void should_parse_when_referenceContainsLocalhostRegistryNameAndTag() {
        Reference reference = Reference.DefaultReference.parse("localhost:5000/alpine:0.0.1");

        assertThat(reference.getRegistry()).isEqualTo("localhost:5000");
        assertThat(reference.getNamespace()).isEqualTo(NAMESPACE_LIBRARY);
        assertThat(reference.getName()).isEqualTo(NAME_ALPINE);
        assertThat(reference.getTag()).contains("0.0.1");
        assertThat(reference.getDigest()).isEmpty();
        assertThat(reference.getTagOrDigest()).isEqualTo("0.0.1");
        assertThat(reference.toString()).isEqualTo("localhost:5000/library/alpine:0.0.1");
    }

}