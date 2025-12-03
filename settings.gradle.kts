@file:Suppress("UnstableApiUsage")

rootProject.name = "krepo"

listOf(
    "frontend",
    "azure-functions",
    "common",
    "rs",
    "test",
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