plugins {
    kotlin("multiplatform") version "2.2.21" apply false
    kotlin("jvm") version "2.1.21" apply false
    kotlin("plugin.serialization") version "2.2.21" apply false
    id("kazure") version "1.0.1-2.2.21" apply false
    id("com.microsoft.azure.azurefunctions") version "1.16.1" apply false
}

val appVersion: String by extra

allprojects {
    group = "cn.rtast.krepo"
    version = appVersion

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://repo.maven.rtast.cn/releases")
    }
}