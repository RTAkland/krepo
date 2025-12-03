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

version = "0.0.3"

kotlin {
    mingwX64()
    jvm()
    macosX64()
}

repositories {
    mavenLocal()
}

publishing {
    repositories {
//        maven("http://lan.rtast.cn:9098/snapshots") {
        maven("http://127.0.0.1:7071/snapshots") {
            isAllowInsecureProtocol = true
            credentials {
                username = "admin"
                password = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwIjoxLCJybiI6OTE5fQ.LsK5P3rgInlHgSWNR835qujz-8ddt2lhzs9LG1lLkO0"
            }
        }
    }
}
