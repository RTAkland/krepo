/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 19:57
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.configManager
import cn.rtast.kmvnrepo.entity.*
import cn.rtast.kmvnrepo.entity.res.CommonDataResponse
import cn.rtast.kmvnrepo.entity.res.CommonResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRepositoriesRouting() {
    routing {
        configureGetPublicRepositoriesRouting()
        configureGetAllRepositoriesRouting()
        authenticate("api") {
            route("/@/api/repositories") {
                configureAddRepositoryRouting()
                configureDeleteRepositoryRouting()
                configureModifyRepositoryRouting()
            }
        }
    }
}

private fun Route.configureModifyRepositoryRouting() {
    put("/modify") {
        val data = call.receive<ModifyRepository>()
        val config = configManager.getConfig()
        if (!config.repositories.any { it.name == data.previousName }) {
            call.respond(HttpStatusCode.NotFound, CommonResponse(404, "仓库不存在"))
        } else {
            configManager.write(
                config.copy(
                    repositories = config.repositories.toMutableList()
                        .apply {
                            removeAll { it.name == data.previousName }
                            add(
                                ConfigRepository(
                                    data.newName,
                                    data.visibility,
                                    data.acceptExtensions,
                                    data.allowSnapshot
                                )
                            )
                        }
                )
            )
            call.respond(HttpStatusCode.OK, CommonResponse(200, "更新成功"))
        }
    }
}

private fun Route.configureDeleteRepositoryRouting() {
    delete("/delete") {
        val repoName = call.receive<DeleteRepository>().name
        val config = configManager.getConfig()
        if (!config.repositories.any { it.name == repoName }) {
            call.respond(HttpStatusCode.NotFound, CommonResponse(404, "仓库不存在"))
        } else {
            configManager.write(
                config.copy(
                    repositories = config.repositories.toMutableList()
                        .apply { removeAll { it.name == repoName } })
            )
            call.respond(HttpStatusCode.OK, CommonResponse(200, "删除成功"))
        }
    }
}

private fun Route.configureAddRepositoryRouting() {
    post("/new") {
        val repositoryInfo = call.receive<CreateRepository>()
        val config = configManager.getConfig()
        if (config.repositories.any { it.name == repositoryInfo.name }) {
            call.respond(HttpStatusCode.Conflict, CommonResponse(409, "当前已有同名仓库: ${repositoryInfo.name}"))
        } else {
            configManager.write(
                config.copy(
                    repositories = config.repositories.toMutableList().apply { add(repositoryInfo) })
            )
            call.respond(HttpStatusCode.OK, CommonResponse(200, "创建成功! 重启后端后生效"))
        }
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