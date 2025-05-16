/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:53
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(InternalAPI::class)

package cn.rtast.krepo.routing.api

import cn.rtast.kmvnrepo.entity.*
import cn.rtast.krepo.REPOSITORY_PATH_STRING
import cn.rtast.krepo.configManager
import cn.rtast.krepo.entity.res.CommonDataResponse
import cn.rtast.krepo.entity.res.CommonResponse
import cn.rtast.krepo.util.file.*
import cn.rtast.krepo.util.string.decodeToByteArrayBase64
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.io.files.Path

fun Application.configureRepositoriesRouting() {
    routing {
        configureGetPublicRepositoriesRouting()
        authenticate("api") {
            route("/@/api/repositories") {
                configureGetAllRepositoriesRouting()
                configureAddRepositoryRouting()
                configureDeleteRepositoryRouting()
                configureModifyRepositoryRouting()
                configureUploadFileRouting()
                configureCreateDirectoryRouting()
            }
        }
    }
}

private fun Route.configureCreateDirectoryRouting() {
    post("/upload") {
        val body = call.receive<UploadFilePayload>()
        val path = Path(body.path, body.filename)
        if (path.exists()) {
            call.respond(HttpStatusCode.Conflict, CommonResponse(409, "Conflict"))
        } else {
            path.writeByteArray(body.file.decodeToByteArrayBase64())
            call.respond(HttpStatusCode.OK, CommonResponse(200, "Success"))
        }
    }
}

private fun Route.configureUploadFileRouting() {
    post("/create-directory") {
        val requestPath = call.receive<CreateDirectoryRequest>().path
        val path = "$REPOSITORY_PATH_STRING/$requestPath".toPath()
        if (path.exists()) {
            call.respond(HttpStatusCode.Conflict, CommonResponse(409, "Conflict"))
        } else {
            path.mkdirs()
            call.respond(HttpStatusCode.OK, CommonResponse(200, "Success"))
        }
    }
}

private fun Route.configureModifyRepositoryRouting() {
    put("/modify") {
        val data = call.receive<ModifyRepository>()
        val currentConfig = configManager.getConfig()
        val config = currentConfig.repositories.find { it.name == data.previousName }
        if (config == null) {
            call.respond(HttpStatusCode.NotFound, CommonResponse(404, "仓库不存在"))
        } else {
            configManager.write(
                currentConfig.copy(
                    repositories = currentConfig.repositories.toMutableList()
                        .apply {
                            removeAll { it.name == data.previousName }
                            add(
                                ConfigRepository(
                                    data.newName,
                                    data.visibility,
                                    data.acceptExtensions,
                                    data.allowSnapshot,
                                    data.status,
                                    config.mirrorRepositories
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
            .toMutableList().apply {
                removeAll { it.visibility == RepositoryVisibility.Internal }
            }
        call.respond(HttpStatusCode.OK, CommonDataResponse(200, repositories))
    }
}

private fun Route.configureGetAllRepositoriesRouting() {
    authenticate("api") {
        get("/all") {
            val repositories = configManager.getConfig().repositories
                .map {
                    ConfigRepositoryWithSize(
                        it.name, it.visibility, it.acceptExtensions, it.allowSnapshot,
                        it.status, Path(REPOSITORY_PATH_STRING, it.name).diskUsageSize
                    )
                }
            call.respond(HttpStatusCode.OK, CommonDataResponse(200, repositories))
        }
    }
}