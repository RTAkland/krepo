@file:Suppress("UnstableApiUsage")

rootProject.name = "kmvnrepo"

listOf(
    "frontend",
    "backend",
    "common",
    "common",
    "clib",
    "test:test-publish",
    "test:test-publish2",
).forEach { include("kmvn-$it") }

pluginManagement {
    repositories {
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