/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity.auth

import kotlinx.serialization.Serializable

@Serializable
data class TokenPayload(
    val name: String,
    val value: String,
    val expiredAt: Long
)