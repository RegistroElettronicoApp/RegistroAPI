plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinNative)
    alias(libs.plugins.ktor)
    kotlin("plugin.serialization") version "1.9.21"
    application
}

group = "me.chicchi7393.registroapi"
version = "1.0.0"
application {
    mainClass.set("me.chicchi7393.registroapi.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

dependencies {
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.curl)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}