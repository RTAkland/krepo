/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/16 20:07
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.routing.api.v2

import cn.rtast.kmvnrepo.entity.V2ArtifactSearchResponse
import cn.rtast.kmvnrepo.entity.res.CommonDataResponse
import cn.rtast.kmvnrepo.util.artifacts.searchMavenRepo
import cn.rtast.kmvnrepo.util.file.isFile
import cn.rtast.kmvnrepo.util.file.isRegularFile
import cn.rtast.kmvnrepo.util.file.rootPathOf
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureV2ArtifactsRouting() {
    routing {
        route("/@/api/v2/artifacts") {
            authenticate("api") {
                searchArtifactRouting()
            }
        }
    }
}

private fun Route.searchArtifactRouting() {
    get("search") {
        val keyword = call.queryParameters["keyword"]!!
        val result = mutableListOf<V2ArtifactSearchResponse.ArtifactSearch>()
        searchMavenRepo(keyword)
            .map { it.removePrefix("repositories/") }
            .forEach {
                result.add(
                    V2ArtifactSearchResponse.ArtifactSearch(
                        it, rootPathOf(it).isRegularFile()
                    )
                )
            }
        call.respond(HttpStatusCode.OK, CommonDataResponse(200, result))
    }
}