/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 19:57
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.configManager
import cn.rtast.kmvnrepo.entity.res.CommonDataResponse
import cn.rtast.kmvnrepo.enums.RepositoryVisibility
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.ExperimentalUuidApi

fun Application.configureRepositoriesRouting() {
    routing {
        configureGetPublicRepositoriesRouting()
        configureGetAllRepositoriesRouting()
    }
}

private fun Route.configureGetPublicRepositoriesRouting() {
    get("/@/api/repositories/public") {
        val repositories = configManager.getConfig().repositories
            .toMutableList().apply { removeAll { it.visibility == RepositoryVisibility.Internal } }
        call.respond(HttpStatusCode.OK, CommonDataResponse(200, repositories))
    }
}

private fun Route.configureGetAllRepositoriesRouting() {
    authenticate("api") {
        get("/@/api/repositories/all") {
            val repositories = configManager.getConfig().repositories
            call.respond(HttpStatusCode.OK, CommonDataResponse(200, repositories))
        }
    }
}