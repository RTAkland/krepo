/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/15 19:31
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class SearchArtifactResponse(
    val count: Int,
    val data: List<SearchArtifact>
) {
    @Serializable
    data class SearchArtifact(
        val group: String,
        val groupSlashed: String,
        val repository: String,
        val artifactId: String,
        val versions: List<String>
    )
}