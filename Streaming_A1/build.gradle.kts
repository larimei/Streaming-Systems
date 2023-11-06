plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.mockito:mockito-core:4.+")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}