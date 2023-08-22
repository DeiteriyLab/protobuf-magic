plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("pl.allegro.tech.build.axion-release") version "1.15.4"
}

project.version = scmVersion.version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.google.guava:guava:31.1-jre")
    implementation("net.portswigger.burp.extensions:montoya-api:2023.1")
    implementation("javax.xml.bind:jaxb-api:2.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.googlecode.protobuf-java-format:protobuf-java-format:1.3")
    implementation("com.google.protobuf:protobuf-java-util:3.19.4")
    implementation("com.github.os72:protobuf-dynamic:1.0.1")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("protobuf.magic.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveFileName.set("protobuf.jar")
    dependsOn("test")
}
