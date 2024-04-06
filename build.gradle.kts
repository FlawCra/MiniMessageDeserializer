import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cc.flawcra"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://nexus.flawcra.cc/repository/maven-mirrors/")
        name = "FlawCra Mirrors"
    }
}

val shadowDependencies = listOf(
    // Adventure
    "net.kyori:adventure-api:" + project.extra["adventure_version"] as String,
    "net.kyori:adventure-text-minimessage:" + project.extra["adventure_version"] as String,
    "net.kyori:adventure-text-serializer-gson:" + project.extra["adventure_version"] as String,

    // Javalin
    "io.javalin:javalin:" + project.extra["javalin_version"] as String,

    // Jackson Databind
    "com.fasterxml.jackson.core:jackson-databind:" + project.extra["jackson_version"] as String,
    "com.fasterxml.jackson.module:jackson-module-kotlin:" + project.extra["jackson_version"] as String,

    // Logback
    "ch.qos.logback:logback-classic:" + project.extra["logback_version"] as String,

    // Sentry
    "io.sentry:sentry-logback:" + project.extra["sentry_version"] as String
)

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    shadowDependencies.forEach { dependency ->
        implementation(dependency)
        shadow(dependency)
    }
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    withType<ShadowJar> {
        mergeServiceFiles()
        configurations = listOf(project.configurations.shadow.get())
        archiveFileName.set("MiniMessageDeserializer.jar")

        manifest {
            attributes["Main-Class"] = "cc.flawcra.mmd.MainKt"
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

kotlin {
    jvmToolchain(21)
}