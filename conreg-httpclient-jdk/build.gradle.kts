plugins {
    id("java-library")
}

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":conreg-core"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")
}

tasks.test {
    useJUnitPlatform()
}