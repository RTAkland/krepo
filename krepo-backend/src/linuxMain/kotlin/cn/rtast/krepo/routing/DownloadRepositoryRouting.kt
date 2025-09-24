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
import cn.rtast.krepo.util.file.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.header
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*

private suspend fun ApplicationCall.serveFile(repository: String) {
    val path = repository + "/" + parameters.getAll("path")!!.joinToString("/")
    val file = rootPathOf(path)
    val fileMetadata = file.metadata()
    val fileSize = fileMetadata.size
    val fileLastModifiedTimestamp = file.getFileModifiedTimestamp()
    val etag = "$fileSize-$fileLastModifiedTimestamp"
    val ifNoneMatchHeader = this.request.header("IfNoneMatch")
    if (ifNoneMatchHeader != null && ifNoneMatchHeader == etag) {
        this.respond(HttpStatusCode.NotModified)
        return
    }
    if (file.exists()) {
        this.response.header("Last-Modified", GMTDate(fileLastModifiedTimestamp * 1000).toHttpDate())
        this.respondSource(file.rawSource(), contentLength = fileSize)
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
