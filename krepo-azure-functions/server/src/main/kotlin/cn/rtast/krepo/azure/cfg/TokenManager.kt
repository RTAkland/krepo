/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 2:28 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.cfg

import cn.rtast.kazure.util.fromJson
import cn.rtast.krepo.azure.entity.internal.CloudflareValue
import cn.rtast.krepo.azure.entity.res.TokenPayload
import cn.rtast.krepo.azure.util.CFKV
import okio.ByteString.Companion.encodeUtf8
import java.util.*
import kotlin.random.Random

class TokenManager {
    fun issue(name: String): TokenPayload {
        val value = UUID.randomUUID().toString().replace("-", "")
            .drop(Random.nextInt(1, 5))
        CFKV.setValue(name.encodeUtf8().hex(), value)
        CFKV.setValue(value.encodeUtf8().hex(), name)
        return TokenPayload(name, value, 3600)
    }

    fun getToken(key: String): String? = CFKV.getValue(key.encodeUtf8().hex())
    fun getUser(key: String): String? = getToken(key)?.fromJson<CloudflareValue>()?.value

    fun validateToken(token: String): Boolean = getToken(token) != null
    fun validateUser(user: String): Boolean = getToken(user) != null
}