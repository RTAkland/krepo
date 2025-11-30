/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/30 19:06
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity

import kotlinx.serialization.Serializable

@Serializable
public data class RepositoryContents(
    val code: Int,
    val count: Int,
    val data: List<FileEntry>
)