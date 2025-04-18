/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/18 21:54
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalForeignApi::class)

package cn.rtast.kmvnrepo.util

import cn.rtast.kmvnrepo.time.get_file_modified_time
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path

actual fun Path.getFileModifiedTimestamp(): Long {
    return try {
        get_file_modified_time(this.toString())
    } catch (_: Exception) {
        -26034180322L
    }
}