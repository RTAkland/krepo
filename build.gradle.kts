plugins {
    kotlin("multiplatform") version "2.1.21" apply false
    kotlin("plugin.serialization") version "2.1.21" apply false
}

val appVersion: String by extra

allprojects {
    group = "cn.rtast.krepo"
    version = appVersion

    repositories {
        google()
        mavenCentral()
    }
}