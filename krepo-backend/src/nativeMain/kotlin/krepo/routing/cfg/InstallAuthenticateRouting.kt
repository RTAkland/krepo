/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:39 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.routing.cfg

import krepo.tokenManager
import krepo.userManager
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