/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:29
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing

import cn.rtast.kmvnrepo.enums.RepositoryVisibility
import cn.rtast.kmvnrepo.repositories
import cn.rtast.kmvnrepo.util.exists
import cn.rtast.kmvnrepo.util.readBytes
import cn.rtast.kmvnrepo.util.rootPathOf
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configureDownloadRouting() {
    repositories.filter { it.visibility == RepositoryVisibility.Public }.forEach {
        get("{path...}") {
            println(call.request.uri)
            val file = rootPathOf(call.request.uri)
            if (file.exists()) call.respondBytes(file.readBytes()) else call.respond(HttpStatusCode.NotFound)
        }
    }
}