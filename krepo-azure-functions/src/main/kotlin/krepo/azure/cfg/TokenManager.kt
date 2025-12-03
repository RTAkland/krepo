/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:48 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalTime::class)

package krepo.azure.cfg

import krepo.azure.entity.internal.TokenPayload
import krepo.azure.userManager
import krepo.azure.util.Jwt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TOKEN_TTL = 3600  // 1 hour

class TokenManager {
    fun issue(name: String, ttl: Int = TOKEN_TTL): TokenPayload {
        val user = userManager.getUser(name)!!
        val token = Jwt.create(user, TOKEN_TTL)
        return TokenPayload(name, token, Clock.System.now().epochSeconds + ttl)
    }

    fun issuePublish(name: String): TokenPayload {
        val token = Jwt.createPublishToken()
        return TokenPayload(name, token, -1)
    }

    fun oauthIssue(uid: String): TokenPayload {
        val user = userManager.getUserByUID(uid)!!
        val token = Jwt.create(user, TOKEN_TTL)
        return TokenPayload(user.name, token, Clock.System.now().epochSeconds + TOKEN_TTL)
    }

    fun validate(token: String): Boolean = Jwt.verify(token, Jwt.TokenPurpose.FULL_ACCESS) != null
}