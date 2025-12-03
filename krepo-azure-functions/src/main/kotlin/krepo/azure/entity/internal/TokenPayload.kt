/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/3 21:34
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.azure.entity.internal

import kotlinx.serialization.Serializable

@Serializable
data class TokenPayload(
    val name: String,
    val value: String,
    val expiredAt: Long,
)