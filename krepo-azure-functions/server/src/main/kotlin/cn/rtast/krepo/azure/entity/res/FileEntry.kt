/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 7:08 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.entity.res

import kotlinx.serialization.Serializable

@Serializable
data class FileEntry(
    val name: String,
    val isDirectory: Boolean,
    val size: Long,
    val timestamp: Long
)