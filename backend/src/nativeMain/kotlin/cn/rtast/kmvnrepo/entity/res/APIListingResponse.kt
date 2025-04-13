/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 06:14
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.entity.res

import cn.rtast.kmvnrepo.entity.FileEntry
import kotlinx.serialization.Serializable

@Serializable
data class APIListingResponse(
    val code: Int,
    val count: Int,
    val data: List<FileEntry>
)