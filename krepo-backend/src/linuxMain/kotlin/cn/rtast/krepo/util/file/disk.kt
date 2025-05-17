/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalForeignApi::class)

package cn.rtast.krepo.util.file

import cn.rtast.krepo.disk.get_disk_usage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path

val Path.diskUsageSize: Long
    get() = get_disk_usage(this.toString())