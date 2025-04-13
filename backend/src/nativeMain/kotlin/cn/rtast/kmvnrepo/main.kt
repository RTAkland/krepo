/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 13:58
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.routing.api.configureAPIArtifactsRouting
import cn.rtast.kmvnrepo.routing.api.configureAPIUserRouting
import cn.rtast.kmvnrepo.routing.configureDownloadRouting
import cn.rtast.kmvnrepo.routing.configureUploadArtifactRouting
import cn.rtast.kmvnrepo.util.ConfigManager
import cn.rtast.kmvnrepo.util.UserManager
import io.ktor.server.auth.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*

val userManager = UserManager()
val configManager = ConfigManager()

fun main() {
    embeddedServer(CIO, configManager.getConfig().port) {
        authentication {
            basic(name = "maven-common") {
                validate { credentials ->
                    if (userManager.validateUser(credentials.name, credentials.password))
                        UserIdPrincipal(credentials.name) else null
                }
            }
        }
        routing {
            configureUploadArtifactRouting()
            configureDownloadRouting()
            configureAPIArtifactsRouting()
            configureAPIUserRouting()
        }
    }.start(wait = true)
}