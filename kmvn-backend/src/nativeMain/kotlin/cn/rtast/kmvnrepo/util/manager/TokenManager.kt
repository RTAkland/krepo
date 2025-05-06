/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/16 15:13
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kmvnrepo.util.manager

import cn.rtast.kmvnrepo.util.generateSecureRandomString
import kotlin.uuid.ExperimentalUuidApi

class TokenManager {
    private val validatedTokens = mutableMapOf<String, String>()

    fun revoke(name: String): String? {
        return validatedTokens.remove(name)
    }

    fun grant(name: String): String {
        return generateSecureRandomString().apply {
            if (validatedTokens.containsKey(name)) {
                validatedTokens.remove(name)
            }
            validatedTokens[name] = this
        }
    }

    fun getName(id: String): String? = validatedTokens.entries.find { it.value == id }?.key

    fun validate(id: String): Boolean = validatedTokens.containsValue(id)
}