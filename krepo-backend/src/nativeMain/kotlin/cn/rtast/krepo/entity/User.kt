/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val email: String,
    val password: String? = null,
)