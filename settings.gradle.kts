@file:Suppress("UnstableApiUsage")

rootProject.name = "krepo"

listOf(
    "backend",
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
        mavenLocal()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.maven.rtast.cn/releases/")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}