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
    kotlin("plugin.serialization")
}

dependencies {
    implementation("software.amazon.awssdk:s3:2.38.2")
    implementation("software.amazon.awssdk:auth:2.38.2")
    implementation("software.amazon.awssdk:netty-nio-client:2.38.2")
    implementation("io.ktor:ktor-client-core:3.3.1")
    implementation("io.ktor:ktor-client-okhttp:3.3.1")
    implementation(project(":krepo-common"))
    implementation("redis.clients:jedis:7.1.0")
    implementation("io.github.pdvrieze.xmlutil:core:0.90.3")
    implementation("io.github.pdvrieze.xmlutil:serialization:0.90.3")
    implementation("org.jetbrains:markdown:0.7.3")
    implementation("com.auth0:java-jwt:4.5.0")
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
}

kembeddable {
    resourcePath.add(File("src/main/resources"))
    packageName = "krepo.azure.resources"
}

tasks.compileKotlin {
    compilerOptions { jvmTarget = JvmTarget.JVM_17 }
}

tasks.compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

@Suppress("DEPRECATION")
tasks.register("killAzureProcesses") {
    doLast {
        val os = System.getProperty("os.name").lowercase()
        println("Killing all processes containing 'azure' in their command line...")
        if (os.contains("win")) {
            try {
                val proc = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq java.exe\"")
                val output = proc.inputStream.bufferedReader().readText().trim()
                val processLines = output.lines().filter { it.contains("azure") }
                if (processLines.isNotEmpty()) {
                    processLines.forEach { line ->
                        val pid = line.split("\\s+".toRegex())[1]
                        println("Killing PID: $pid")
                        Runtime.getRuntime().exec("taskkill /PID $pid /F")
                    }
                } else {
                    println("No azure-related processes found.")
                }
            } catch (e: Exception) {
                println("Error killing processes: ${e.message}")
            }
        } else {
            try {
                val proc = Runtime.getRuntime()
                    .exec(arrayOf("sh", "-c", "ps -ef | grep azure | grep -v grep | awk '{print $2}'"))
                val output = proc.inputStream.bufferedReader().readText().trim()
                if (output.isNotEmpty()) {
                    output.lines().forEach { pid ->
                        println("Killing PID: $pid")
                        Runtime.getRuntime().exec("kill -9 $pid").waitFor()
                    }
                } else {
                    println("No azure-related processes found.")
                }
            } catch (e: Exception) {
                println("Error killing processes: ${e.message}")
            }
        }
    }
}


/**
 * on linux azure functions process affects incorrectly,
 * so must kill the process to run the functions.
 * macOS is not tested.
 */
tasks.named("azureFunctionsRun") {
    finalizedBy("killAzureProcesses")
}

tasks.named("azureFunctionsPackage") {
    dependsOn(tasks.named("generateResources"))
}