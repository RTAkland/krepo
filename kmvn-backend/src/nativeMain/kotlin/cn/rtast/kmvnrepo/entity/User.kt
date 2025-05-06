/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:51
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val password: String? = null,
    val email: String,
)