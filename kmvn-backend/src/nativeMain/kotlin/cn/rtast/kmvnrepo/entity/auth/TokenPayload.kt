/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/9 03:49
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity.auth

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class TokenPayload(
    val name: String,
    val value: String,
    val expiredAt: Long
)