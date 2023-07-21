plugins {
    id("java")
}

dependencies {
    annotationProcessor("info.picocli:picocli-codegen:4.7.4")

    implementation(project(":conreg-core"))
    implementation(project(":conreg-httpclient-jdk"))

    implementation("info.picocli:picocli:4.7.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}