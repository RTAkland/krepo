/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:50
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package cn.rtast.krepo.util.manager

import cn.rtast.krepo.entity.auth.TokenPayload
import cn.rtast.krepo.util.generateSecureRandomString
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

class TokenManager {
    private val validatedTokens = mutableListOf<TokenPayload>()
    private val mutex = Mutex()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        startScheduler()
    }

    private suspend fun cleanupExpiredTokens() {
        val now = Clock.System.now().epochSeconds
        mutex.withLock {
            validatedTokens.removeAll { it.expiredAt <= now }
        }
    }

    private fun startScheduler() {
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
    }

    suspend fun revoke(name: String) {
        mutex.withLock {
            validatedTokens.removeAll { it.name == name }
        }
    }

    /**
     * @param name token所属用户名
     * @param tokenDurationSeconds token有效期，单位秒
     * @return Pair(tokenValue, expiredAtEpochSeconds)
     */
    suspend fun grant(name: String, tokenDurationSeconds: Long): Pair<String, Long> {
        cleanupExpiredTokens()
        val token = generateSecureRandomString()
        mutex.withLock {
            validatedTokens.removeAll { it.name == name }
            val expiredAt = Clock.System.now().epochSeconds + tokenDurationSeconds
            validatedTokens.add(TokenPayload(name, token, expiredAt))
            return token to expiredAt
        }
    }

    suspend fun getName(id: String): String? = mutex.withLock {
        validatedTokens.find { it.value == id }?.name
    }

    suspend fun validate(id: String): Boolean {
        cleanupExpiredTokens()
        val now = Clock.System.now().epochSeconds
        return mutex.withLock {
            val payload = validatedTokens.find { it.value == id } ?: return false
            if (payload.expiredAt <= now) {
                validatedTokens.remove(payload)
                false
            } else true
        }
    }
}