/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/16 15:36
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity.res

import kotlinx.serialization.Serializable

@Serializable
data class LogoutResponse(
    val code: Int,
    val message: String
)