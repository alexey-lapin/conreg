pluginManagement {
    includeBuild("gradle/plugins")
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "conreg"
include("conreg-core")
include("conreg-httpclient-jdk")
include("conreg-cli")
