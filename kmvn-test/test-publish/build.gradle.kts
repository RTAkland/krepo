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
        maven("http://127.0.0.1:9098/snapshots") {
            isAllowInsecureProtocol = true
            credentials {
                username = "admin"
                password = "42262a85-dffa-4898-a944-170690e124b2"
            }
        }
    }
}
