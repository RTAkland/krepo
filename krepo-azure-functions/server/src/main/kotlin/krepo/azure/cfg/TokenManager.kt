/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:48 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalTime::class)

package krepo.azure.cfg

import krepo.azure.entity.res.TokenPayload
import krepo.azure.storageManager
import java.security.SecureRandom
import java.util.Base64
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TOKEN_TTL = 3600  // 1 hour

class TokenManager {
    private fun generateSecureToken(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun issue(name: String): TokenPayload {
        val token = this.generateSecureToken()
        storageManager.setKV(token, name, TOKEN_TTL)
        return TokenPayload(name, token, Clock.System.now().epochSeconds + TOKEN_TTL)
    }

    fun revoke(token: String) {
        storageManager.removeKV(token)
    }

    fun validate(token: String): Boolean = storageManager.getKV(token) != null
}