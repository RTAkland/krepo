import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:04 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp") version "2.2.21-2.0.4"
}

val fritz2Version = "1.0-RC20"
val ktorVersion = "3.1.2"

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "krepo-frontend.js"
                sourceMaps = false
             }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":krepo-common"))
        }
        jsMain {
            dependencies {
                implementation("dev.fritz2:core:$fritz2Version")
                implementation("dev.fritz2:headless:$fritz2Version")
            }
        }
    }
}

dependencies.kspCommonMainMetadata("dev.fritz2:lenses-annotation-processor:$fritz2Version")
kotlin.sourceSets.commonMain { tasks.withType<KspTaskMetadata> { kotlin.srcDir(destinationDirectory) } }

val isDevelopmentModeTask by tasks.registering {
    group = "kotlin browser"
    if (System.getenv("ACTIONS") != null) {
        println("Development mode is off")
        project.layout.projectDirectory.dir("src/jsMain/kotlin/krepo/development.kt")
            .asFile.writeText("package krepo\npublic val developmentMode = false")
    } else {
        println("Development mode is on")
        project.layout.projectDirectory.dir("src/jsMain/kotlin/krepo/development.kt")
            .asFile.writeText("package krepo\npublic val developmentMode = true")
    }
}

tasks.named("jsBrowserDevelopmentRun") {
    dependsOn(isDevelopmentModeTask)
}

tasks.named("jsBrowserDistribution") {
    dependsOn(isDevelopmentModeTask)
    if (System.getenv("CLOUDFLARE_API_TOKEN") == null) {
        val file = project(":krepo-backend").layout
            .projectDirectory.dir("src/nativeMain/resources/res_version.txt").asFile
        val originContent = file.readText()
        val version = file.readLines().first().split("=").last()
        file.writeText(originContent.replace(version, (version.toInt() + 1).toString()))
        println("Resources version bumped")
    }
    finalizedBy("copyDistForWorker", "copyDistForVercel")
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