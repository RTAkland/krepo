/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 05:53
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.entity.FileEntry
import cn.rtast.kmvnrepo.entity.res.APIListingResponse
import cn.rtast.kmvnrepo.internalRepositories
import cn.rtast.kmvnrepo.publicRepositories
import cn.rtast.kmvnrepo.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private suspend fun ApplicationCall.handleRequest() {
    val fullPath = rootPathOf(request.uri.replace("/api/listing/", ""))
    if (!fullPath.exists()) {
        respondText(APIListingResponse(-200, 0, emptyList()).toJson(), ContentType.Application.Json)
        return
    }
    val entries = fullPath.listFiles().map {
        FileEntry(
            name = it.name,
            isDirectory = it.isDirectory(),
            size = if (it.isFile()) it.size() else -1
        )
    }
    respondText(APIListingResponse(200, entries.size, entries).toJson(), ContentType.Application.Json)
}


fun Routing.configureAPIRepositoryRouting() {
    publicRepositories.forEach {
        route("/api/listing/${it.name}") {
            get("{path...}") { call.handleRequest() }
        }
    }

    internalRepositories.forEach {
        route("/api/listing/${it.name}") {
            authenticate("maven-common") {
                get("{path...}") { call.handleRequest() }
            }
        }
    }
}