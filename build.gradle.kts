plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.ksp) apply false
}

val appVersion: String by extra

allprojects {
    group = "krepo"
    version = appVersion

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://repo.maven.rtast.cn/releases")
    }
}