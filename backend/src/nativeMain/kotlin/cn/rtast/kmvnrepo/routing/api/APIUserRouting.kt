/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 00:52
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.entity.User
import cn.rtast.kmvnrepo.entity.res.UserAPIResponseMessage
import cn.rtast.kmvnrepo.userManager
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAPIUserRouting() {
    install(CORS) {
        anyHost()
    }
    routing {
        authenticate("maven-common") {
            get("/api/auth/test") {
                call.respond(UserAPIResponseMessage(200, "测试成功"))
            }

            post("/api/user") {
                val user = call.receive<User>()
                if (userManager.addUser(user)) {
                    call.respond(UserAPIResponseMessage(200, "添加成功 -> ${user.name}"))
                } else {
                    call.respond(UserAPIResponseMessage(-200, "添加失败, 该用户已存在"))
                }
            }

            delete("/api/user") {
                val user = call.receive<User>()
                if (userManager.removeUser(user.name)) {
                    call.respond(UserAPIResponseMessage(200, "删除成功"))
                } else {
                    call.respond(UserAPIResponseMessage(-200, "删除失败, 该用户不存在"))
                }
            }
        }
    }
}