/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/29 21:11
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import krepo.azure.cfg.ConfigManger
import krepo.entity.user.User
import java.util.*

object Jwt {
    fun create(user: User, ttl: Int): String {
        val expirationTime = Date(System.currentTimeMillis() + ttl * 1000)
        val token = JWT.create()
            .withSubject(user.name)
            .withIssuer("krepo")
            .withIssuedAt(Date())
            .withExpiresAt(expirationTime)
            .sign(Algorithm.HMAC256(ConfigManger.JWT_SECRET))
        return token
    }

    fun verify(token: String): DecodedJWT? {
        return try {
            val algorithm = Algorithm.HMAC256(ConfigManger.JWT_SECRET)
            val verifier = JWT.require(algorithm).build()
            val decodedJWT = verifier.verify(token)
            decodedJWT
        } catch (e: JWTVerificationException) {
            println("Invalid or expired token: ${e.message}")
            null
        }
    }
}