plugins {
    id("java-library")
}

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":conreg-core"))

    testImplementation(libs.jackson.databind)
    testImplementation(libs.jackson.datatype.jsr310)
    testImplementation(libs.gson)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)

    testRuntimeOnly(libs.logback.classic)
}

tasks.compileTestJava {
    targetCompatibility = JavaVersion.VERSION_17.toString()
    sourceCompatibility = JavaVersion.VERSION_17.toString()
}

tasks.test {
    useJUnitPlatform()
}