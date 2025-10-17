/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:52
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.krepo.routing

import cn.rtast.krepo.configManager
import cn.rtast.krepo.entity.res.CommonResponse
import cn.rtast.krepo.publicRepositories
import cn.rtast.krepo.util.respondJson
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
