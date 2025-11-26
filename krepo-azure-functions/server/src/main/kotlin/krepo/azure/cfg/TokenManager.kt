/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:48 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalTime::class)

package krepo.azure.cfg

import cn.rtast.kazure.util.fromJson
import krepo.azure.entity.internal.CloudflareValue
import krepo.azure.entity.res.TokenPayload
import krepo.azure.util.CFKV
import okio.ByteString.Companion.encodeUtf8
import java.util.*
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TOKEN_TTL = 3600  // 1 hour

class TokenManager {

    fun issue(name: String): TokenPayload {
        val value = UUID.randomUUID().toString().replace("-", "")
            .drop(Random.nextInt(1, 5))
        CFKV.setValue(name.encodeUtf8().hex(), value, TOKEN_TTL)
        CFKV.setValue(value.encodeUtf8().hex(), name, TOKEN_TTL)
        return TokenPayload(name, value, Clock.System.now().epochSeconds + TOKEN_TTL)
    }

    fun getToken(key: String): String? = CFKV.getValue(key.encodeUtf8().hex())
    fun getUser(key: String): String? = getToken(key)?.fromJson<CloudflareValue>()?.value

    fun validateToken(token: String): Boolean = getToken(token) != null
    fun validateUser(user: String): Boolean = getToken(user) != null
}