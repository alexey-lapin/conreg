plugins {
    id("java-library")
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.projectlombok:lombok:1.18.26")

    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.15.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}