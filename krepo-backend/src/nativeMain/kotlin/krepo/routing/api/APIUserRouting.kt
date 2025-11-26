/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:40 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.routing.api

import krepo.entity.User
import krepo.entity.res.*
import krepo.tokenManager
import krepo.userManager
import krepo.util.respondJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
                val (token, expiredAt) = tokenManager.grant(username, 3600)
                call.respondJson(
                    AuthSuccessResponse(
                        200,
                        "登陆成功",
                        token,
                        user.email,
                        user.name,
                        expiredAt
                    )
                )
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
                call.respondJson(LogoutResponse(200, "登出成功"))
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
                    call.respondJson(CommonResponse(200, "用户添加成功 -> ${user.name}"))
                } else {
                    call.respondJson(CommonResponse(-200, "用户添加失败, 该用户已存在"), 401)
                }
            }

            delete("/@/api/user/{username}") {
                try {
                    val username = call.parameters["username"]!!
                    if (userManager.removeUser(username)) {
                        call.respondJson(CommonResponse(200, "用户已被删除"))
                    } else {
                        call.respondJson(CommonResponse(-200, "用户删除失败, 该用户不存在"), 404)
                    }
                } catch (e: Exception) {
                    call.respondJson(CommonResponse(-2000, "用户删除失败, ${e.message}"), 400)
                }
            }
        }
    }
}