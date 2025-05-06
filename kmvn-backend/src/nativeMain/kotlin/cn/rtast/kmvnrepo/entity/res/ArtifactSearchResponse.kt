/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/16 02:13
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity.res

import kotlinx.serialization.Serializable

@Serializable
data class ArtifactSearchResponse(
    val code: Int,
    val message: String,
    val count: Int,
    val data: List<Artifact>
) {
    @Serializable
    data class Artifact(
        val group: String,
        val groupSlashed: String,
        val repository: String,
        val artifactId: String,
        val versions: List<String>
    )
}