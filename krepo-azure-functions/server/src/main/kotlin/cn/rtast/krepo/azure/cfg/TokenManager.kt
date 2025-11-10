/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 2:28 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.cfg

import cn.rtast.krepo.azure.entity.res.TokenPayload
import cn.rtast.krepo.azure.util.CFKV
import java.util.*
import kotlin.random.Random

class TokenManager {
    fun issue(name: String): TokenPayload {
        val value = UUID.randomUUID().toString().replace("-", "")
            .drop(Random.nextInt(1, 5))
        CFKV.setValue(name, value)
        return TokenPayload(name, value, 3600)
    }

    fun check(name: String, token: String): Boolean {
        return CFKV.getValue(name) == token
    }
}