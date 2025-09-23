/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 21:38
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class GetUsersResponse(
    val code: Int,
    val data: List<User>
) {
    @Serializable
    data class User(
        val name: String,
        val email: String
    )
}

@Serializable
data class GetUserResponse(
    val code: Int,
    val data: GetUsersResponse.User
)