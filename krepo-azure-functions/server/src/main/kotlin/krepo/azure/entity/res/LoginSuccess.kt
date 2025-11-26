/*
 * Copyright © 2025 RTAkland
 * Date: 11/12/25, 8:57 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.entity.res

data class LoginSuccess(
    val code: Int,
    val message: String,
    val token: String,
    val email: String,
    val username: String,
    val expiredAt: Long
)