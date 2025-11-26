/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:39 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalUuidApi::class)

package krepo.routing

import krepo.configManager
import krepo.entity.res.CommonResponse
import krepo.publicRepositories
import krepo.util.respondJson
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.ExperimentalUuidApi

fun Application.configurePublicRepositoriesListing() {
    if (!configManager.getConfig().allowFileListing) {
        publicRepositories.forEach {
            routing {
                route("/listing/${it.name}") {
                    get("{path...}") {
                        call.respondJson(CommonResponse(403, "文件列出已被禁止"), 403)
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
