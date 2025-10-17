/*
 * Copyright © 2025 RTAkland
 * Date: 9/23/25, 1:45 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.routing.cfg

import cn.rtast.krepo.tokenManager
import cn.rtast.krepo.userManager
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.auth.bearer

fun Application.installAuthenticateRouting() {
    install(Authentication) {
        bearer("api") {
            authenticate { tokenCredential ->
                val id = tokenCredential.token
                if (tokenManager.validate(id)) {
                    val validatedUser = tokenManager.getName(id)!!
                    UserIdPrincipal(validatedUser)
                } else null
            }
        }
        basic(name = "maven-common") {
            validate { credentials ->
                if (userManager.validateUser(credentials.name, credentials.password))
                    UserIdPrincipal(credentials.name) else null
            }
        }
    }
}