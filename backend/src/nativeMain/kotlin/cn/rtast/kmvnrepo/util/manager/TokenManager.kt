/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/16 15:13
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kmvnrepo.util.manager

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TokenManager {
    private val validatedTokens = mutableMapOf<String, Uuid>()

    fun revoke(name: String): Uuid? {
        return validatedTokens.remove(name)
    }

    fun grant(name: String): Uuid {
        return Uuid.random().apply {
            if (validatedTokens.containsKey(name)) {
                validatedTokens.remove(name)
            }
            validatedTokens[name] = this
        }
    }

    fun getName(id: Uuid): String? = validatedTokens.entries.find { it.value == id }?.key

    fun validate(id: Uuid): Boolean = validatedTokens.containsValue(id)
}