/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 03:07
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kmvnrepo.routing

import cn.rtast.kmvnrepo.configManager
import cn.rtast.kmvnrepo.entity.res.CommonResponse
import cn.rtast.kmvnrepo.publicRepositories
import cn.rtast.kmvnrepo.tokenManager
import cn.rtast.kmvnrepo.userManager
import cn.rtast.kmvnrepo.util.manager.i18n
import cn.rtast.kmvnrepo.util.respondJson
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.ExperimentalUuidApi

fun Application.configurePublicRepositoriesListing() {
    install(ContentNegotiation) {
        json()
    }
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

    if (!configManager.getConfig().allowFileListing) {
        publicRepositories.forEach {
            routing {
                route("/listing/${it.name}") {
                    get("{path...}") {
                        call.respondJson(CommonResponse(403, call.i18n("file-listing-disabled")), 403)
                    }
                }
            }
        }
    } else {
        publicRepositories.forEach {
            routing {
                route("/listing/${it.name}") {
                    get("{path...}") {
                        val pathParts = (call.parameters.getAll("path")
                            ?.joinToString("/") ?: "")
                        call.respondRedirect("${configManager.getConfig().frontend}/#/${it.name}${if (!pathParts.isBlank()) "/" else ""}$pathParts")
                    }
                }
            }
        }
    }
}
