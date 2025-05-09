/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:08
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")
@file:OptIn(ExperimentalForeignApi::class, ExperimentalTime::class, ExperimentalNativeApi::class)

package cn.rtast.kmvnrepo.util.string

import cn.rtast.kmvnrepo.ROOT_PATH
import io.ktor.utils.io.core.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlin.experimental.ExperimentalNativeApi
import kotlin.time.ExperimentalTime

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

fun Path.rawSource() = SystemFileSystem.source(this)

fun rootPathOf(path: String): Path {
    return Path(ROOT_PATH, path)
}

fun Path.exists(): Boolean = SystemFileSystem.exists(this)

fun Path.isFile(): Boolean = SystemFileSystem.metadataOrNull(this)!!.isRegularFile

fun Path.isDirectory() = SystemFileSystem.metadataOrNull(this)!!.isDirectory

fun Path.listFiles() = SystemFileSystem.list(this)

fun Path.size() = SystemFileSystem.metadataOrNull(this)!!.size

fun Path.delete() = SystemFileSystem.delete(this, false)

fun Path.deleteRec() = deleteFolderRecursively(this)

fun Path.metadata() = SystemFileSystem.metadataOrNull(this)!!

fun deleteFolderRecursively(path: Path) {
    val metadata = SystemFileSystem.metadataOrNull(path) ?: return
    if (metadata.isDirectory) {
        SystemFileSystem.list(path).forEach { child ->
            deleteFolderRecursively(child)
        }
    }
    SystemFileSystem.delete(path)
}

fun Path.searchDirectory(name: String): MutableList<Path> {
    val result = mutableListOf<Path>()
    fun search(dir: Path) {
        if (!dir.isDirectory()) return
        if (dir.name.contains(name)) {
            result.add(dir)
        }
        dir.listFiles().filter { it.isDirectory() }.forEach {
            search(it)
        }
    }
    search(this)
    return result
}

fun String.toPath() = Path(this)

fun Path.calculateDirectorySize(): Float {
    var totalSize = 0f
    this.listFiles().forEach {
        totalSize += if (it.isDirectory()) {
            it.calculateDirectorySize()
        } else {
            it.metadata().size.toFloat()
        }
    }
    return totalSize
}

expect fun Path.getFileModifiedTimestamp(): Long

expect fun Path.isRegularFile(): Boolean

expect fun Path.cIsDirectory(): Boolean
