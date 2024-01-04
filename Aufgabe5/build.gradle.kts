plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("ch.qos.logback:logback-classic:1.4.11")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    implementation("org.apache.kafka:kafka-clients:3.4.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}