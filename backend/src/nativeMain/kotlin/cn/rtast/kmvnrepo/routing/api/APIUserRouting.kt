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
import cn.rtast.kmvnrepo.util.respondJson
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
    }
    routing {
        authenticate("maven-common") {
            post("/@/api/login") {
                val username = call.principal<UserIdPrincipal>()?.name!!
                val token = tokenManager.grant(username)
                call.respondJson(AuthSuccessResponse(200, "登陆成功", token.toString()))
            }
        }

        authenticate("api") {
            post("/@/api/logout") {
                tokenManager.revoke(call.principal<UserIdPrincipal>()!!.name)
                call.respondJson(LogoutResponse(200, "登出成功"))
            }

            get("/@/api/user") {
                val credential = call.principal<UserIdPrincipal>()!!.name
                call.respondJson(CommonResponse(200, credential))
            }

            post("/@/api/user") {
                val user = call.receive<User>()
                if (userManager.addUser(user)) {
                    call.respond(CommonResponse(200, "添加成功 -> ${user.name}"))
                } else {
                    call.respond(CommonResponse(-200, "添加失败, 该用户已存在"))
                }
            }

            delete("/@/api/user/{username}") {
                try {
                    if (userManager.removeUser(call.parameters["username"]!!)) {
                        call.respond(CommonResponse(200, "删除成功"))
                    } else {
                        call.respond(CommonResponse(-200, "删除失败, 该用户不存在"))
                    }
                } catch (e: Exception) {
                    call.respond(CommonResponse(-2000, "删除失败, ${e.message}"))
                }
            }
        }
    }
}