/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 14:17
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    js(IR) { browser() }
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
            api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
        }
    }
}