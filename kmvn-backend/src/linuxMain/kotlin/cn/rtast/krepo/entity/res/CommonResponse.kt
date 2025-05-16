/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.entity.res

import kotlinx.serialization.Serializable

@Serializable
data class CommonResponse(
    val code: Int,
    val message: String
)