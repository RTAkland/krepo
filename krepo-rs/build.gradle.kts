/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 4:15 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

val projectDir = project.layout.projectDirectory
val outputDir = project.layout.buildDirectory.get().asFile
val rustTargetDir = projectDir.dir("target")

val executableName = project.name

val includeDist = arrayOf("*.dll", "*.so", "*.dylib", "*.a", executableName)

val copyReleaseOutputs = tasks.register<Copy>("copyReleaseOutputs") {
    dependsOn("buildRelease")
    from("${projectDir.asFile}/target/release") { include(*includeDist) }
    into(File(outputDir, "releases"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    rename { fileName ->
        when (fileName) {
            executableName -> "${fileName}-release"
            "$executableName.exe" -> "${fileName.removeSuffix(".exe")}-release.exe"
            else -> fileName
        }
    }
}

val copyDebugOutputs = tasks.register<Copy>("copyDebugOutputs") {
    dependsOn("buildDebug")
    from("${projectDir.asFile}/target/debug") { include(*includeDist) }
    into(File(outputDir, "debug"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    rename { fileName ->
        when (fileName) {
            executableName -> "${fileName}-debug"
            "$executableName.exe" -> "${fileName.removeSuffix(".exe")}-debug.exe"
            else -> fileName
        }
    }
}

val copyRustOutputs = tasks.register("copyRustOutputs") {
    dependsOn(copyReleaseOutputs)
    dependsOn(copyDebugOutputs)
}

tasks.register("build") {
    group = "build"
    dependsOn(copyRustOutputs)
}

val buildRelease = tasks.register<Exec>("buildRelease") {
    group = "build"
    workingDir = projectDir.asFile
    commandLine("cargo", "build", "--release")
}

val buildDebug = tasks.register<Exec>("buildDebug") {
    group = "build"
    workingDir = projectDir.asFile
    commandLine("cargo", "build")
}

tasks.register("clean") {
    group = "build"
    outputDir.deleteRecursively()
}

tasks.register("cleanRust") {
    group = "build"
    rustTargetDir.asFile.deleteRecursively()
}