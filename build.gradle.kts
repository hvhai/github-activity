plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    application
}

group = "com.codehunter.github_activity_kotlin"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.codehunter.github_activity_kotlin.MainKt")
}
tasks.withType<JavaExec> {
    standardInput = System.`in`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}