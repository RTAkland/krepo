/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 00:52
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.entity.User
import cn.rtast.kmvnrepo.entity.res.AuthSuccessResponse
import cn.rtast.kmvnrepo.entity.res.CommonResponse
import cn.rtast.kmvnrepo.entity.res.LogoutResponse
import cn.rtast.kmvnrepo.tokenManager
import cn.rtast.kmvnrepo.userManager
import cn.rtast.kmvnrepo.util.manager.i18n
import cn.rtast.kmvnrepo.util.respondJson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlin.uuid.ExperimentalUuidApi

fun Application.configureAPIUserRouting() {
    install(CORS) {
        anyHost()
    }
    routing {
        authenticate("maven-common") {
            post("/@/api/login") {
                val username = call.principal<UserIdPrincipal>()?.name!!
                val token = tokenManager.grant(username)
                call.respondJson(AuthSuccessResponse(200, call.i18n("login"), token.toString()))
            }
        }

        authenticate("api") {
            post("/@/api/logout") {
                val username = call.principal<UserIdPrincipal>()!!.name
                tokenManager.revoke(username)
                call.respondJson(LogoutResponse(200, call.i18n("logout")))
            }

            get("/@/api/user") {
                val credential = call.principal<UserIdPrincipal>()!!.name
                call.respondJson(CommonResponse(200, credential))
            }

            post("/@/api/user") {
                val user = call.receive<User>()
                if (userManager.addUser(user)) {
                    call.respondJson(CommonResponse(200, "${call.i18n("user.add.success")} -> ${user.name}"))
                } else {
                    call.respondJson(CommonResponse(-200, call.i18n("user.add.failed")), 401)
                }
            }

            delete("/@/api/user/{username}") {
                try {
                    val username = call.parameters["username"]!!
                    if (userManager.removeUser(username)) {
                        call.respondJson(CommonResponse(200, call.i18n("user.delete.success")))
                    } else {
                        call.respondJson(CommonResponse(-200, call.i18n("user.delete.failure.not.exists")), 404)
                    }
                } catch (e: Exception) {
                    call.respondJson(CommonResponse(-2000, "${call.i18n("user.delete.failure")}, ${e.message}"), 400)
                }
            }
        }
    }
}