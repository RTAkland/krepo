@file:OptIn(ExperimentalWasmDsl::class)

import com.google.devtools.ksp.gradle.KspTaskMetadata
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
    id("com.google.devtools.ksp") version "2.1.20-2.0.0"
}

val fritz2Version = "1.0-RC20"
val ktorVersion = "3.1.2"

repositories {
    mavenCentral()
    maven("https://repo.maven.rtast.cn/releases/")
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "main.bundle.js"
                sourceMaps = false
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation("dev.fritz2:core:$fritz2Version")
            implementation("io.ktor:ktor-client-cio:$ktorVersion")
            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            implementation("cn.rtast.rutil:string:0.0.1")
        }
    }
}

dependencies.kspCommonMainMetadata("dev.fritz2:lenses-annotation-processor:$fritz2Version")
kotlin.sourceSets.commonMain { tasks.withType<KspTaskMetadata> { kotlin.srcDir(destinationDirectory) } }

val isDevelopmentModeTask by tasks.registering {
    if (System.getenv("_DEVELOPMENT_MODE") != null) {
        project.layout.projectDirectory.dir("src/jsMain/kotlin/cn/rtast/kmvnrepo/development.kt")
            .asFile.writeText("package cn.rtast.kmvnrepo\npublic const val developmentMode = true")
    } else {
        project.layout.projectDirectory.dir("src/jsMain/kotlin/cn/rtast/kmvnrepo/development.kt")
            .asFile.writeText("package cn.rtast.kmvnrepo\npublic const val developmentMode = false")
    }
}

tasks.all {
    if (this.name != "isDevelopmentModeTask") {
        this.dependsOn(isDevelopmentModeTask)
    }
}