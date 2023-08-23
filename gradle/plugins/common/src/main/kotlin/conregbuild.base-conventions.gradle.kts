import pl.allegro.tech.build.axion.release.domain.VersionConfig

plugins {
    id("java-library")
    id("pl.allegro.tech.build.axion-release") apply false
}

version = rootProject.extensions.getByType(VersionConfig::class).version