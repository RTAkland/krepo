/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/9 23:00
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class ProxiedRepository(
    val url: String,
    val credential: Credential?,
) {
    @Serializable
    data class Credential(
        val username: String,
        val password: String
    )
}