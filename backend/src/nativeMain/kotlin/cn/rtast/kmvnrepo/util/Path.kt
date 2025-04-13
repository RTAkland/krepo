/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:08
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")
@file:OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)

package cn.rtast.kmvnrepo.util

import cn.rtast.kmvnrepo.ROOT_PATH
import io.ktor.utils.io.core.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.cinterop.*
import platform.posix.*

fun Path.mkdirs(): Path {
    SystemFileSystem.createDirectories(this)
    return this
}

fun Path.writeText(content: String) {
    SystemFileSystem.sink(this).buffered().use { it.writeText(content) }
}

fun Path.writeByteArray(bytes: ByteArray) {
    SystemFileSystem.sink(this).buffered().use { it.write(bytes) }
}

fun Path.readText(): String {
    return SystemFileSystem.source(this).buffered().use { it.readText() }
}

fun Path.readBytes(): ByteArray {
    return SystemFileSystem.source(this).buffered().use { it.readByteArray() }
}

fun rootPathOf(path: String): Path {
    return Path(ROOT_PATH, path)
}

fun Path.exists(): Boolean = SystemFileSystem.exists(this)

fun Path.isFile(): Boolean = SystemFileSystem.metadataOrNull(this)!!.isRegularFile

fun Path.isDirectory() = SystemFileSystem.metadataOrNull(this)!!.isDirectory

fun Path.listFiles() = SystemFileSystem.list(this)
