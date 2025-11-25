/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:42
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

import cn.rtast.kembeddable.resources.gradle.util.NativeMain
import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("cn.rtast.kembeddable") version "1.3.8"
    id("cn.rtast.kdef") version "0.1.1"
    id("com.google.devtools.ksp") version "2.2.21-2.0.4"
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
            entryPoint = "cn.rtast.krepo.main"
            baseName = "krepo-$targetName-${buildType.name.lowercase()}-$version"
        }
        compilerOptions.freeCompilerArgs.add("-Xallocator=std")
    }

    val ktorVersion = "3.1.2"

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
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
    group = "kotlin browser"
    if (System.getenv("CLOUDFLARE_API_TOKEN") == null) {
        println("Development mode is off")
        project.layout.projectDirectory.dir("src/jsMain/kotlin/cn/rtast/krepo/development.kt")
            .asFile.writeText("package cn.rtast.krepo\npublic val developmentMode = false")
    } else {
        println("Development mode is on")
        project.layout.projectDirectory.dir("src/jsMain/kotlin/cn/rtast/krepo/development.kt")
            .asFile.writeText("package cn.rtast.krepo\npublic val developmentMode = true")
    }
}

kembeddable {
    compression = false
    resourcePath.apply {
        add(NativeMain)
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

tasks.named("jsBrowserDistribution") {
    dependsOn(isDevelopmentModeTask)
    if (System.getenv("CLOUDFLARE_API_TOKEN") == null) {
        val file = project.layout.projectDirectory.dir("src/nativeMain/resources/res_version.txt")
            .asFile
        val originContent = file.readText()
        val version = file.readLines().first().split("=").last()
        file.writeText(originContent.replace(version, (version.toInt() + 1).toString()))
        println("Resources version bumped")
    }
    finalizedBy("copyDistForWorker", "copyDistForVercel")
}

tasks.named("runDebugExecutableLinuxX64") {
    dependsOn(tasks.named("generateResources"))
}

fun Task.copyDistToPlatformDir(
    platform: String,
    target: String? = null,
    files: Task.(dist: File, target: File) -> Unit,
) {
    val distDir = project.layout.buildDirectory.dir("dist/js/productionExecutable")
        .get().asFile
    val targetDir = project.layout.buildDirectory.dir("$platform-dist")
        .get().asFile.apply {
            deleteRecursively()
            mkdirs()
        }
    inputs.dir(distDir)
    outputs.dir(targetDir)
    doLast {
        copy {
            from(distDir) {
                exclude("*.LICENSE.txt")
                exclude("serverless/**")
            }
            if (target != null) {
                into(File(targetDir, target))
            } else {
                into(targetDir)
            }
        }
        this@copyDistToPlatformDir.files(distDir, targetDir)
    }
}

tasks.register("copyDistForVercel") {
    copyDistToPlatformDir("vercel") { dist, target ->
        File(dist, "serverless/vercel/vercel.json").copyTo(File(target, "vercel.json"))
    }
}

tasks.register("copyDistForWorker") {
    copyDistToPlatformDir("worker", "dist") { dist, target ->
        File(dist, "serverless/workers/worker.js").copyTo(File(target, "worker.js"))
        File(dist, "serverless/workers/wrangler.jsonc").copyTo(File(target, "wrangler.jsonc"))
    }
}