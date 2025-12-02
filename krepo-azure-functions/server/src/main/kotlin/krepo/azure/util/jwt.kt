/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/29 21:11
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import krepo.azure.cfg.ConfigManger
import krepo.azure.util.Jwt.TokenPurpose.Companion.toPurpose
import krepo.azure.util.Jwt.TokenPurpose.Companion.withPurpose
import krepo.entity.user.User
import java.util.*
import kotlin.random.Random

object Jwt {
    enum class TokenPurpose(val s: Int) {
        PUBLISH(1), FULL_ACCESS(9);

        companion object {
            fun Int.toPurpose(): TokenPurpose? {
                return when (this) {
                    1 -> PUBLISH
                    9 -> FULL_ACCESS
                    else -> null
                }
            }

            fun JWTCreator.Builder.withPurpose(p: TokenPurpose): JWTCreator.Builder =
                this.withClaim("p", p.s)

        }
    }

    fun create(user: User, ttl: Int): String {
        val expirationTime = Date(System.currentTimeMillis() + ttl * 1000)
        val token = JWT.create()
            .withSubject(user.name)
            .withIssuer("krepo")
            .withIssuedAt(Date())
            .withExpiresAt(expirationTime)
            .withPurpose(TokenPurpose.FULL_ACCESS)
            .sign(Algorithm.HMAC256(ConfigManger.JWT_SECRET))
        return token
    }

    fun createPublishToken(): String {
        val token = JWT.create()
            .withPurpose(TokenPurpose.PUBLISH)
            .withClaim("rn", Random.nextInt(0, 1000))
            .sign(Algorithm.HMAC256(ConfigManger.JWT_SECRET))
        return token
    }

    fun verify(token: String, purpose: TokenPurpose): DecodedJWT? {
        return try {
            val algorithm = Algorithm.HMAC256(ConfigManger.JWT_SECRET)
            val verifier = JWT.require(algorithm).build()
            val decodedJWT = verifier.verify(token)
            val p = decodedJWT.getClaim("p").asInt().toPurpose() ?: return null
            if (p != purpose) return null
            decodedJWT
        } catch (e: JWTVerificationException) {
            println("Invalid or expired token: ${e.message}")
            null
        }
    }
}