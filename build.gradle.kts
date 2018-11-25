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
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.5.0")
    implementation("io.reactivex.rxjava2:rxkotlin:2.2.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.12.0")
}

val jar: Jar by tasks
jar.apply {
    from(configurations["embed"].map {
        if (it.isDirectory) it as Any else zipTree(it)
    })
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
