/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 23:55
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

    sourceSets {
        commonMain.dependencies {
//            implementation("cn.rtast.kmvnrepo:test-publish:0.0.3")
        }

        jvmMain.dependencies {
            implementation("cn.rtast.jvmonly-linter:cn.rtast.jvmonly-linter.gradle.plugin:0.1.18")
        }
    }
}

repositories {
    mavenLocal()
    maven("http://127.0.0.1:9098/releases") {
        isAllowInsecureProtocol = true
    }
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
