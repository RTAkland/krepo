/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:30
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    kotlin("multiplatform") version "2.1.20"
    id("maven-publish")
}

version = "0.0.2"

kotlin {
    mingwX64()
    jvm()
}

repositories {
    mavenLocal()
    maven("http://127.0.0.1:9098/releases")
}

publishing {
    repositories {
        maven("http://127.0.0.1:9098/releases") {
            credentials {
                username = "RTAkland"
                password = "123456"
            }
        }
    }
}
