import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
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

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine")

    testImplementation ("org.mockito:mockito-core:3.6.28")
    testImplementation ("org.mockito:mockito-inline:3.6.28")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}