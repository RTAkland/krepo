/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 13:58
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.routing.configureDownloadRouting
import cn.rtast.kmvnrepo.routing.configureUploadArtifactRouting
import cn.rtast.kmvnrepo.util.UserManager
import io.ktor.server.auth.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*

val userManager = UserManager()

fun main() {
    embeddedServer(CIO, 9098) {
        authentication {
            basic(name = "maven-upload") {
                validate { credentials ->
                    if (userManager.validateUser(credentials.name, credentials.password))
                        UserIdPrincipal(credentials.name) else null
                }
            }
        }
        routing {
            configureUploadArtifactRouting()
            configureDownloadRouting()
        }
    }.start(wait = true)
}