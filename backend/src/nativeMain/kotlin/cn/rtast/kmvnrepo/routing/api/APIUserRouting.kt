/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 00:52
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.entity.User
import cn.rtast.kmvnrepo.entity.res.*
import cn.rtast.kmvnrepo.tokenManager
import cn.rtast.kmvnrepo.userManager
import cn.rtast.kmvnrepo.util.manager.i18n
import cn.rtast.kmvnrepo.util.respondJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.ExperimentalUuidApi

fun Application.configureAPIUserRouting() {
    install(CORS) {
        anyHost()
        anyMethod()
        allowNonSimpleContentTypes = true
        allowHeaders { true }
    }
    routing {
        authenticate("maven-common") {
            post("/@/api/login") {
                val username = call.principal<UserIdPrincipal>()?.name!!
                val user = userManager.getUser(username)!!
                val token = tokenManager.grant(username)
                call.respondJson(AuthSuccessResponse(200, call.i18n("login"), token, user.email, user.name))
            }
        }

        authenticate("api") {
            put("/@/api/user/{username}") {
                val userInfo = call.receive<User>()
                userManager.removeUser(userInfo.name)
                userManager.addUser(userInfo)
                call.respond(HttpStatusCode.OK, CommonResponse(200, "修改成功"))
            }

            post("/@/api/logout") {
                val username = call.principal<UserIdPrincipal>()!!.name
                tokenManager.revoke(username)
                call.respondJson(LogoutResponse(200, call.i18n("logout")))
            }

            get("/@/api/user") {
                val credential = call.principal<UserIdPrincipal>()!!.name
                call.respondJson(CommonResponse(200, credential))
            }

            get("/@/api/user/") {
                val users = userManager.getAllUsers().map { NoSensitiveUser(it.name, it.email) }
                call.respond(HttpStatusCode.OK, CommonDataResponse(200, users))
            }

            get("/@/api/user/{name}") {
                val user = userManager.getUser(call.parameters["name"]!!)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, CommonDataResponse(404, null))
                } else {
                    call.respond(HttpStatusCode.OK, CommonDataResponse(200, NoSensitiveUser(user.name, user.email)))
                }
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