plugins {
    id("java")
}

dependencies {
    annotationProcessor(libs.picocli)

    implementation(project(":conreg-core"))
    implementation(project(":conreg-httpclient-jdk"))

    implementation(libs.picocli.codegen)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jsr310)

    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}