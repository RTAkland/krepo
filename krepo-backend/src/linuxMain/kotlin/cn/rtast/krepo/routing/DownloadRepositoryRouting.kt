/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:51
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.routing

import cn.rtast.krepo.internalRepositories
import cn.rtast.krepo.publicRepositories
import cn.rtast.krepo.registry.getProxiedArtifacts
import cn.rtast.krepo.repositories
import cn.rtast.krepo.util.file.exists
import cn.rtast.krepo.util.file.rawSource
import cn.rtast.krepo.util.file.rootPathOf
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private suspend fun ApplicationCall.serveFile(repository: String) {
    val path = repository + "/" + parameters.getAll("path")!!.joinToString("/")
    val file = rootPathOf(path)
    if (file.exists()) {
        this.respondSource(file.rawSource())
    } else getProxiedArtifacts(
        repositories.find { it.name == repository }!!.mirrorRepositories,
        parameters.getAll("path")!!.joinToString("/"),
        repository
    )
}

fun Application.configureDownloadRouting() {
    routing {
        publicRepositories.forEach {
            route(it.name) {
                get("{path...}") { call.serveFile(it.name) }
            }
        }

        internalRepositories.forEach {
            authenticate("maven-common") {
                route(it.name) {
                    get("{path...}") { call.serveFile(it.name) }
                }
            }
        }
    }
}
