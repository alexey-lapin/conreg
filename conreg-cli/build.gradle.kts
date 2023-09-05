plugins {
    id("java")
    id("application")
    id("conregbuild.base-conventions")
    alias(libs.plugins.shadow)
}

application {
    mainClass.set("com.github.alexeylapin.conreg.cli.ConReg")
}

dependencies {
    annotationProcessor(libs.picocli.codegen)

    implementation(project(":conreg-core"))
    implementation(project(":conreg-httpclient-jdk"))

    implementation(libs.picocli)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jsr310)

    testImplementation(libs.junit.jupiter)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}