/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:42
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

import cn.rtast.kembeddable.resources.gradle.util.NativeMain
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("cn.rtast.kembeddable") version "1.3.8"
    id("cn.rtast.kdef") version "0.1.1"
}

val fritz2Version = "1.0-RC20"
val ktorVersion = "3.1.2"

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

    macosX64 {
        compilations["main"].cinterops {
            val fileTimeLinux by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/file_time_macosx64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
            val diskUsageLinux by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/disk_usage_macosx64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
            val file by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/file_macosx64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
        }
    }

    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "krepo-backend.js"
                sourceMaps = false
            }
        }
        binaries.executable()
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.executable {
            entryPoint = "krepo.main"
            baseName = "krepo-backend-$targetName-${buildType.name.lowercase()}-$version"
        }
        compilerOptions.freeCompilerArgs.add("-Xallocator=std")
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        commonMain.dependencies {
            implementation(project(":krepo-common"))
        }

        nativeMain.dependencies {
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
    compression = false
    resourcePath.apply {
        add(NativeMain)
        add(project(":krepo-frontend").layout.buildDirectory.dir("dist/js/productionExecutable").get().asFile)
//        add(project.layout.buildDirectory.dir("dist/js/developmentExecutable").get().asFile)
    }
    packageName = "krepo.fr.resources"
    publicGeneratedResourceVariable = true
}

kdef {
    outputDir = project.layout.projectDirectory.dir("src/cinterop/def").asFile
    defFiles.addAll(
        project.layout.projectDirectory.dir("src/cinterop/def/template")
            .asFile.listFiles()!!.toList()
    )
}

tasks.named("runDebugExecutableLinuxX64") {
    dependsOn(tasks.named("generateResources"))
}