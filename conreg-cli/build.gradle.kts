import org.graalvm.buildtools.gradle.tasks.BuildNativeImageTask

plugins {
    id("java")
    id("application")
    id("conregbuild.base-conventions")
    alias(libs.plugins.graalvm)
    alias(libs.plugins.shadow)
}

application {
    mainClass.set("com.github.alexeylapin.conreg.cli.ConReg")
}

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set(rootProject.name)
        }
    }
}

dependencies {
    annotationProcessor(libs.picocli.codegen)

    implementation(project(":conreg-core"))
    implementation(project(":conreg-httpclient-jdk"))

    implementation(libs.picocli)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jsr310)

    runtimeOnly(libs.logback.classic)

    testImplementation(libs.junit.jupiter)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}

val writeArtifactFile by tasks.registering {
    doLast {
        val outputDirectory = tasks.getByName<BuildNativeImageTask>("nativeCompile").outputDirectory
        outputDirectory.get().asFile.mkdirs()
        outputDirectory.file("gradle-artifact.txt")
                .get().asFile
                .writeText("${project.name}-${project.version}")
    }
}

tasks.getByName("nativeCompile") {
    finalizedBy(writeArtifactFile)
}