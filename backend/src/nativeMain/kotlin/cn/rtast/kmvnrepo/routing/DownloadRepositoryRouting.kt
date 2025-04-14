/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:29
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing

import cn.rtast.kmvnrepo.internalRepositories
import cn.rtast.kmvnrepo.publicRepositories
import cn.rtast.kmvnrepo.util.exists
import cn.rtast.kmvnrepo.util.readBytes
import cn.rtast.kmvnrepo.util.rootPathOf
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private suspend fun ApplicationCall.serveFile() {
    val file = rootPathOf(request.uri)
    if (file.exists()) this.respondBytes(file.readBytes()) else this.respond(HttpStatusCode.NotFound)
}

fun Application.configureDownloadRouting() {
    routing {
        publicRepositories.forEach {
            route(it.name) {
                get("{path...}") { call.serveFile() }
            }
        }

        internalRepositories.forEach {
            authenticate("maven-common") {
                route(it.name) {
                    get("{path...}") { call.serveFile() }
                }
            }
        }
    }
}
