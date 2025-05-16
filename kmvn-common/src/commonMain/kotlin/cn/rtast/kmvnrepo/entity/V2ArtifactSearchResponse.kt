/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/16 21:42
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class V2ArtifactSearchResponse(
    val code: Int,
    val data: List<ArtifactSearch>
) {
    @Serializable
    data class ArtifactSearch(
        val path: String,
        val isFile: Boolean
    )
}