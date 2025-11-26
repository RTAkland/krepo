import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*
 * Copyright © 2025 RTAkland
 * Date: 11/25/25, 12:30 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    linuxArm64()
    linuxX64()
    macosX64()
    js(IR) {
        browser()
    }
    jvm { compilerOptions.jvmTarget = JvmTarget.JVM_17 }

    explicitApi()

    sourceSets {
        commonMain.dependencies {
            api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
            api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
        }
    }
}