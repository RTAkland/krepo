import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:42
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    linuxX64()
    linuxArm64()
    mingwX64()

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.executable {
            entryPoint = "cn.rtast.kmvnrepo.main"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.7.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
            implementation("io.github.pdvrieze.xmlutil:core:0.91.0")
            implementation("io.github.pdvrieze.xmlutil:serialization:0.91.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("io.ktor:ktor-server-core:3.1.2")
            implementation("io.ktor:ktor-server-cio:3.1.2")
            implementation("io.ktor:ktor-server-auth:3.1.2")
        }
    }
}