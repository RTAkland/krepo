/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:52
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.entity.res

import cn.rtast.krepo.entity.FileEntry
import kotlinx.serialization.Serializable

@Serializable
data class APIListingResponse(
    val code: Int,
    val count: Int,
    val data: List<FileEntry>
)