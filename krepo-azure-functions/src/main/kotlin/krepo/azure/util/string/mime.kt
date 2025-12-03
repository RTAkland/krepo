/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/3 16:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util.string

internal val mimeTypeTable = mapOf(
    "json" to "application/json",
    "xml" to "application/xml",
    "zip" to "application/zip",
    "tar" to "application/x-tar",
    "gz" to "application/gzip",
    "java" to "text/x-java-source",
    "jar" to "application/java-archive",
    "pom" to "application/xml",
    "war" to "application/java-archive",
    "class" to "application/java",
    "exe" to "application/octet-stream",
    "dll" to "application/octet-stream",
    "klib" to "application/octet-stream",
    "apk" to "application/vnd.android.package-archive",
    "so" to "application/x-sharedlib",
    "dylib" to "application/x-dylib",
    "framework" to "application/x-framework",
    "a" to "application/x-archive",
    "md5" to "text/plain",
    "sha1" to "text/plain",
    "sha256" to "text/plain",
    "sha512" to "text/plain",
    "module" to "application/json",
    "kt" to "text/x-kotlin"
)

internal val String.mimeType
    get() = mimeTypeTable[this.substringAfterLast('.', "").lowercase()] ?: "application/octet-stream"