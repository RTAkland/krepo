/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/30 19:05
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity

import kotlinx.serialization.Serializable

@Serializable
public data class FileEntry(
    val name: String,
    val isDirectory: Boolean,
    val size: Long,
    val timestamp: Long,
    val owner: String? = null
)