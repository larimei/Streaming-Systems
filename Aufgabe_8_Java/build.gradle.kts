plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.espertech:esper-common:8.9.0")
    implementation("com.espertech:esper-runtime:8.9.0")
    implementation("com.espertech:esper-compiler:8.9.0")

}

tasks.test {
    useJUnitPlatform()
}