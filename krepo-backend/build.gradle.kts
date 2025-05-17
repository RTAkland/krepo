/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:42
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("cn.rtast.kembeddable") version "1.2.4"
    id("cn.rtast.kdef") version "0.1.1"
}

repositories {
    mavenCentral()
    maven("https://repo.maven.rtast.cn/releases")
}

kotlin {
    linuxX64 {
        compilations["main"].cinterops {
            val fileTimeLinux by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/file_time_linuxx64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
            val diskUsageLinux by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/disk_usage_linuxx64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
            val file by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/file_linuxx64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.executable {
            entryPoint = "cn.rtast.krepo.main"
        }
        compilerOptions.freeCompilerArgs.add("-Xallocator=std")
    }

    val ktorVersion = "3.1.2"

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        linuxMain.dependencies {
            implementation(project(":krepo-common"))
            implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.7.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("io.ktor:ktor-server-auto-head-response:${ktorVersion}")
            implementation("io.ktor:ktor-server-core:$ktorVersion")
            implementation("io.ktor:ktor-server-cio:$ktorVersion")
            implementation("io.ktor:ktor-server-auth:$ktorVersion")
            implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            implementation("io.ktor:ktor-server-cors:$ktorVersion")
            implementation("io.ktor:ktor-client-core:${ktorVersion}")
            implementation("io.ktor:ktor-server-status-pages:${ktorVersion}")
            implementation("io.github.pdvrieze.xmlutil:core:0.90.3")
            implementation("io.github.pdvrieze.xmlutil:serialization:0.90.3")
            implementation("io.ktor:ktor-client-curl:$ktorVersion")
        }
    }
}

kembeddable {
    compression = true
    resourcePath.add("linuxMain/resources")
    packageName = "cn.rtast.krepo.resources"
}

tasks.all {
    when (name) {
        "runDebugExecutableMingwX64",
        "deployBackend" -> dependsOn(tasks.named("generateResources"))
    }
}

kdef {
    outputDir = project.layout.projectDirectory.dir("src/cinterop/def").asFile
    defFiles.addAll(
        project.layout.projectDirectory.dir("src/cinterop/def/template")
            .asFile.listFiles()!!.toList())
}