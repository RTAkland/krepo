/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 2:36 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.entity.res


data class TokenPayload(
    val name: String,
    val value: String,
    val expiredAt: Long
)