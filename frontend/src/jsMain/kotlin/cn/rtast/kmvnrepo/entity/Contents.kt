/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/17 00:12
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class Contents(
    val data: List<Content>
) {
    @Serializable
    data class Content(
        val name: String,
        val isDirectory: Boolean,
        val size: Long,
        val timestamp: Long
    )
}