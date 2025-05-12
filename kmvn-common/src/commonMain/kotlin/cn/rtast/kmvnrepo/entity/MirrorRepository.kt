/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/12 20:56
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class MirrorRepository(
    val url: String,
    val credential: Credential?,
) {
    @Serializable
    data class Credential(
        val username: String,
        val password: String
    )
}