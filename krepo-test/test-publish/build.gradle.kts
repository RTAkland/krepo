/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:30
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("maven-publish")
}

version = "0.0.1"

kotlin {
    mingwX64()
    jvm()
}

repositories {
    mavenLocal()
}

publishing {
    repositories {
//        maven("http://lan.rtast.cn:9098/snapshots") {
        maven("http://127.0.0.1:9098/releases") {
            isAllowInsecureProtocol = true
            credentials {
                username = "admin"
                password = "b521452d-bae3-4f11-8d39-9496cb1e09da"
            }
        }
    }
}
