/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 00:52
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.entity.User
import cn.rtast.kmvnrepo.entity.UserSession
import cn.rtast.kmvnrepo.entity.res.CommonResponse
import cn.rtast.kmvnrepo.userManager
import cn.rtast.kmvnrepo.util.respondJson
import cn.rtast.kmvnrepo.util.toJson
import cn.rtast.kmvnrepo.util.validateSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlin.time.Duration.Companion.hours

suspend fun ApplicationCall.unauthorized() {
    respondText(contentType = ContentType.Application.Json, text = CommonResponse(401, "请先登录").toJson())
}

fun Application.configureAPIUserRouting() {
    install(CORS) {
        anyHost()
    }
    install(Sessions) {
        cookie<UserSession>("user_session", SessionStorageMemory()) {
            cookie.maxAge = 6.hours
        }
    }

    routing {
        authenticate("maven-common") {
            post("/@/api/login") {
                val username = call.principal<UserIdPrincipal>()?.name!!
                call.sessions.set(UserSession(username))
                call.respondJson(CommonResponse(200, "登陆成功"))
            }
        }

        get("/@/api/user") {
            validateSession {
                call.respondText("test")
            }
        }

        post("/@/api/user") {
            validateSession {
                val user = call.receive<User>()
                if (userManager.addUser(user)) {
                    call.respond(CommonResponse(200, "添加成功 -> ${user.name}"))
                } else {
                    call.respond(CommonResponse(-200, "添加失败, 该用户已存在"))
                }
            }
        }

        delete("/@/api/user/{username}") {
            validateSession {
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