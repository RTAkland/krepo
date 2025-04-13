/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:10
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.util

import kotlinx.io.files.Path


fun writeArtifact(path: String, bytes: ByteArray) {
    try {
        val splitPath = path.split("/")
        val filename = splitPath.last()
        val dir = splitPath.dropLast(1).joinToString("/")
        Path(rootPathOf(dir).apply { mkdirs() }, filename).writeByteArray(bytes)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}