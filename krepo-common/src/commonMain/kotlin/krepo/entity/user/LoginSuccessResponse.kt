/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 11:07 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity.user

import kotlinx.serialization.Serializable

@Serializable
public data class LoginSuccessResponse(
    val code: Int,
    val message: String,
    val token: String,
    val email: String,
    val username: String,
    val expiredAt: Long,
    val avatarMd5: String?
)