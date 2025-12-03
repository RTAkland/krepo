import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*
 * Copyright © 2025 RTAkland
 * Date: 11/25/25, 12:30 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    js(IR) { browser() }
    jvm { compilerOptions.jvmTarget = JvmTarget.JVM_17 }

    explicitApi()

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.serialization.core)
            api(libs.kotlinx.serialization.protobuf)
        }
    }
}