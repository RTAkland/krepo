import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 5:24 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    kotlin("jvm")
    id("kazure")
    id("com.google.devtools.ksp")
    id("com.microsoft.azure.azurefunctions")
}

dependencies {
    implementation("software.amazon.awssdk:s3:2.38.2")
    implementation("software.amazon.awssdk:auth:2.38.2")
    implementation("software.amazon.awssdk:netty-nio-client:2.38.2")
    implementation("io.ktor:ktor-client-core:3.3.1")
    implementation("io.ktor:ktor-client-okhttp:3.3.1")
    implementation(project(":krepo-common"))
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xcontext-parameters"))
    }
}

azurefunctions {
    appName = "krepo-server"
    resourceGroup = "krepo-server_group"
}

kazure {
    listingResources = false
    excludeFiles.addAll("users.json", "users.template.json")
    resourceRoutingPrefix = "frontend/"
}

tasks.compileKotlin {
    compilerOptions { jvmTarget = JvmTarget.JVM_17 }
}

tasks.compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

val frontendDir = project.layout.projectDirectory.dir("src/main/resources/frontend").asFile.apply {
    mkdirs()
}

tasks.register("compileAndCopyFrontendAssets") {
    group = "krepo"
    dependsOn(":krepo-backend:jsBrowserDistribution")
    doLast {
        val sourceDir = project(":krepo-backend").layout.buildDirectory
            .dir("dist/js/productionExecutable").get().asFile
        project.copy {
            from(sourceDir)
            into(frontendDir)
        }
        logger.lifecycle("Copied frontend assets.")
    }
}

tasks.register("killAzureProcesses") {
    doLast {
        println("Killing all processes containing 'azure' in their command line...")
        val proc =
            Runtime.getRuntime().exec(arrayOf("sh", "-c", "ps -ef | grep azure | grep -v grep | awk '{print \$2}'"))
        val output = proc.inputStream.bufferedReader().readText().trim()
        if (output.isNotEmpty()) {
            output.lines().forEach { pid ->
                println("Killing PID: $pid")
                @Suppress("DEPRECATION")
                Runtime.getRuntime().exec("kill -9 $pid").waitFor()
            }
        } else {
            println("No azure-related processes found.")
        }
    }
}

tasks.named("azureFunctionsRun") {
    finalizedBy("killAzureProcesses")
}