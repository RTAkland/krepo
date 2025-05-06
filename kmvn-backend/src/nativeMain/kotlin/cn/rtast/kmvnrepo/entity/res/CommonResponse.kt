/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 19:53
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity.res

import kotlinx.serialization.Serializable

@Serializable
data class CommonResponse(
    val code: Int,
    val message: String
)