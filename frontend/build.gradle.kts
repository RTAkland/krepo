@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:41
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    wasmJs {
        browser()
    }
}