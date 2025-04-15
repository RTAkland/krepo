/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/16 01:53
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util

import cn.rtast.kmvnrepo.entity.UserSession
import cn.rtast.kmvnrepo.routing.api.unauthorized
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

suspend fun RoutingContext.validateSession(block: suspend () -> Unit) {
    if (call.sessions.get<UserSession>() == null) {
        call.unauthorized()
    } else {
        block()
    }
}