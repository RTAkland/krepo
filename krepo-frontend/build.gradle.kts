import com.google.devtools.ksp.gradle.KspTaskMetadata

/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:04 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ksp)
}

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
                implementation(libs.fritz2.core)
                implementation(libs.fritz2.headless)
            }
        }
    }
}

dependencies.kspCommonMainMetadata("dev.fritz2:lenses-annotation-processor:${libs.versions.fritz2.get()}")
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