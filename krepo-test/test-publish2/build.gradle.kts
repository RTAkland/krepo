/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 23:55
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("maven-publish")
}

version = "0.0.2"
group = "cn.rtast.krepo"

kotlin {
    mingwX64()
//    jvm()

    sourceSets {
        commonMain.dependencies {
//            implementation("cn.rtast.kmvnrepo:test-publish:0.0.3")
        }
    }
}

repositories {
    mavenLocal()
//    maven("http://127.0.0.1:9098/releases")
    maven("http://192.168.10.222:9098/snapshots") {
        isAllowInsecureProtocol = true
    }
}

publishing {
    repositories {
        maven("http://127.0.0.1:9098/releases") {
            credentials {
                username = "admin"
                password = "b521452d-bae3-4f11-8d39-9496cb1e09da"
            }
        }
    }
}
