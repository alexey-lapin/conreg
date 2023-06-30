plugins {
    id("java-library")
}

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")

    implementation(project(":conreg-core"))
    implementation("org.slf4j:jul-to-slf4j:2.0.5")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")
    testImplementation("org.slf4j:jul-to-slf4j:2.0.5")
    testImplementation("ch.qos.logback:logback-classic:1.4.7")
}

tasks.test {
    useJUnitPlatform()
}