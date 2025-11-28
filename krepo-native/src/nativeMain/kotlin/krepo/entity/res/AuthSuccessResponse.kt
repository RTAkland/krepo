/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity.res

import kotlinx.serialization.Serializable

@Serializable
data class AuthSuccessResponse(
    val code: Int,
    val message: String,
    val token: String,
    val email: String,
    val username: String,
    val expiredAt: Long
)