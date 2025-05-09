/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/16 15:13
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package cn.rtast.kmvnrepo.util.manager

import cn.rtast.kmvnrepo.entity.auth.TokenPayload
import cn.rtast.kmvnrepo.util.generateSecureRandomString
import kotlinx.coroutines.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

class TokenManager {
    private val validatedTokens = mutableListOf<TokenPayload>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        this.startScheduler()
    }

    private fun cleanupExpiredTokens() {
        val now = Clock.System.now().epochSeconds
        validatedTokens.removeAll { it.expiredAt <= now }
    }

    fun startScheduler() =
        coroutineScope.launch {
            while (isActive) {
                try {
                    delay(5.minutes)
                    cleanupExpiredTokens()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    fun revoke(name: String) {
        validatedTokens.removeAll { it.name == name }
    }

    fun grant(name: String): String {
        this.cleanupExpiredTokens()
        return generateSecureRandomString().apply {
            if (validatedTokens.any { it.name == name }) {
                this@TokenManager.revoke(name)
            }
            validatedTokens.add(TokenPayload(name, this, Clock.System.now().epochSeconds + 7 * 24 * 60 * 60))
        }
    }

    fun getName(id: String): String? = validatedTokens.find { it.value == id }?.name

    fun validate(id: String): Boolean {
        this.cleanupExpiredTokens()
        val payload = validatedTokens.find { it.value == id } ?: return false
        if (payload.expiredAt <= Clock.System.now().epochSeconds) {
            this.revoke(payload.name)
            return false
        } else return true
    }
}