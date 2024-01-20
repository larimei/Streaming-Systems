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
    implementation ("org.jetbrains.kotlin:kotlin-stdlib")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    implementation("org.apache.kafka:kafka-clients:3.4.0")
    implementation("com.espertech:esper-common:8.9.0")
    implementation ("com.espertech:esper-runtime:8.9.0")
    implementation ("com.espertech:esper-compiler:8.9.0")
    implementation("com.espertech:esperio-kafka:8.9.0")

    testImplementation(kotlin("test"))

    testImplementation ("org.slf4j:slf4j-simple:1.7.32")

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine")

    testImplementation ("org.mockito:mockito-core:3.6.28")
    testImplementation ("org.mockito:mockito-inline:3.6.28")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}