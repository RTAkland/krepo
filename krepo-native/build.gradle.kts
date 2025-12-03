/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:42
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

import cn.rtast.kembeddable.resources.gradle.util.NativeMain

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kembeddable)
    alias(libs.plugins.kdef)
}

val ktorVersion = "3.1.2"

kotlin {
    listOf(
        mingwX64(),
        linuxX64(),
        linuxArm64(),
        macosX64()
    ).forEach {
        it.binaries.executable {
            entryPoint = "krepo.main"
            baseName = "krepo-backend-${it.targetName}-${buildType.name.lowercase()}-$version"
        }
        it.compilerOptions.freeCompilerArgs.add("-Xallocator=std")

        it.compilations["main"].cinterops {
            val fileTime by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/file_time_${it.name}.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
            val diskUsage by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/disk_usage_${it.name}.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
            val file by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/def/file_${it.name}.def").asFile
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

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        commonMain.dependencies {
            implementation(project(":krepo-common"))
        }

        nativeMain.dependencies {
            implementation(libs.kotlinx.io.core)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.auth)
            implementation(libs.ktor.server.auto.head.response)
            implementation(libs.ktor.server.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.server.cors)
            implementation(libs.ktor.server.status.pages)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.curl)

            implementation(libs.xmlutil.core)
            implementation(libs.xmlutil.serialization)
            implementation("org.kotlincrypto.hash:md:0.8.0")
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