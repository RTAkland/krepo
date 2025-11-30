@file:Suppress("UnstableApiUsage")

rootProject.name = "krepo"

listOf(
    "native",
    "frontend",
    "test:test-publish",
    "test:test-publish2",
    "azure-functions:server",
    "azure-functions:auth",
    "common",
    "rs"
).forEach { include("krepo-$it") }

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.maven.rtast.cn/releases/")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.maven.rtast.cn/releases/")
    }
}