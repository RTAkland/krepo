/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:42
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

import cn.rtast.kembeddable.resources.gradle.util.LinuxMain
import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("cn.rtast.kembeddable") version "1.3.6"
    id("cn.rtast.kdef") version "0.1.1"
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

val fritz2Version = "1.0-RC20"
val ktorVersion = "3.1.2"

repositories {
    mavenCentral()
    maven("https://repo.maven.rtast.cn/releases")
}

kotlin {
    linuxArm64 {
        compilations["main"].cinterops {
            val fileTimeLinux by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/file_time_arm64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
            val diskUsageLinux by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/disk_usage_arm64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
            val file by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/file_arm64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
        }
    }

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

    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "krepo-backend.js"
            }
        }
        binaries.executable()
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

        commonMain.dependencies {
            implementation(project(":krepo-common"))
        }

        linuxMain.dependencies {
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

        jsMain {
            kotlin.setSrcDirs(kotlin.srcDirs - file(project.layout.buildDirectory.dir("generated")))
            dependencies {
                implementation("dev.fritz2:core:$fritz2Version")
                implementation("dev.fritz2:headless:$fritz2Version")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("cn.rtast.rutil:string:0.0.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
                implementation("org.kotlincrypto.hash:md:0.6.1")
                implementation("org.jetbrains:markdown:0.7.3")
            }
        }
    }
}

dependencies.kspCommonMainMetadata("dev.fritz2:lenses-annotation-processor:$fritz2Version")
kotlin.sourceSets.commonMain { tasks.withType<KspTaskMetadata> { kotlin.srcDir(destinationDirectory) } }

val isDevelopmentModeTask by tasks.registering {
    if (System.getenv("_DEVELOPMENT_MODE") != null) {
        project.layout.projectDirectory.dir("src/jsMain/kotlin/cn/rtast/krepo/development.kt")
            .asFile.writeText("package cn.rtast.krepo\npublic const val developmentMode = true")
    } else {
        project.layout.projectDirectory.dir("src/jsMain/kotlin/cn/rtast/krepo/development.kt")
            .asFile.writeText("package cn.rtast.krepo\npublic const val developmentMode = false")
    }
}

kembeddable {
    compression = false
    resourcePath.apply {
        add(project.layout.buildDirectory.dir("dist/js/productionExecutable").get().asFile)
//        add(project.layout.buildDirectory.dir("dist/js/developmentExecutable").get().asFile)
    }
    packageName = "cn.rtast.krepo.fr.resources"
    publicGeneratedResourceVariable = true
}

kdef {
    outputDir = project.layout.projectDirectory.dir("src/cinterop/def").asFile
    defFiles.addAll(
        project.layout.projectDirectory.dir("src/cinterop/def/template")
            .asFile.listFiles()!!.toList()
    )
}