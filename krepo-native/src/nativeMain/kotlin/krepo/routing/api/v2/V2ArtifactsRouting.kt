/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:40 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.routing.api.v2

import krepo.entity.res.CommonDataResponse
import krepo.util.artifacts.searchMavenRepo
import krepo.util.file.isRegularFile
import krepo.util.file.rootPathOf
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.response.*
import io.ktor.server.routing.*
import krepo.entity.maven.V2ArtifactSearchResponse

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