import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.10"
    id("org.spongepowered.plugin") version "0.8.1"
}

group = "com.m4kvn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val embed by configurations.creating {
    extendsFrom(configurations["implementation"])
}

dependencies {
    embed(kotlin("stdlib-jdk8"))
    implementation("org.spongepowered:spongeapi:7.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val jar: Jar by tasks
jar.apply {
    from(configurations["embed"].map {
        if (it.isDirectory) it as Any else zipTree(it)
    })
}