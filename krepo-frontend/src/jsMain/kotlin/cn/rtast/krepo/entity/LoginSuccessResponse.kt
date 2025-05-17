/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 17:10
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class LoginSuccessResponse(
    val code: Int,
    val message: String,
    val token: String,
    val email: String,
    val username: String
)