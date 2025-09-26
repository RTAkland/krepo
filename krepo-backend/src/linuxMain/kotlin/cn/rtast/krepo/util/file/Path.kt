/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:51
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")
@file:OptIn(ExperimentalForeignApi::class, ExperimentalTime::class, ExperimentalNativeApi::class)

package cn.rtast.krepo.util.file

import cn.rtast.krepo.REPOSITORY_PATH
import cn.rtast.krepo.file.is_directory
import cn.rtast.krepo.file.is_regular_file
import cn.rtast.krepo.time.get_file_modified_time
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.*
import io.ktor.utils.io.readAvailable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.Buffer
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

fun Path.writeTextBuffered(content: String) {
    val buffer = Buffer().apply { writeText(content) }
    SystemFileSystem.sink(this).write(buffer, buffer.size)
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
    return Path(REPOSITORY_PATH, path)
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

fun Path.getFileModifiedTimestamp(): Long {
    return try {
        get_file_modified_time(this.toString())
    } catch (_: Exception) {
        -26034180322L
    }
}

fun Path.isRegularFile(): Boolean {
    return is_regular_file(this.toString()) != 0
}

fun Path.cIsDirectory(): Boolean {
    return is_directory(this.toString()) != 0
}

suspend fun Path.writeStreamToFile(channel: ByteReadChannel) {
    val sink = SystemFileSystem.sink(this).buffered()
    try {
        val buffer = ByteArray(8192)
        while (!channel.isClosedForRead) {
            val bytesRead = channel.readAvailable(buffer, 0, buffer.size)
            if (bytesRead == -1) break
            if (bytesRead > 0) {
                sink.write(buffer, 0, bytesRead)
            }
        }
        sink.flush()
    } finally {
        sink.close()
    }
}