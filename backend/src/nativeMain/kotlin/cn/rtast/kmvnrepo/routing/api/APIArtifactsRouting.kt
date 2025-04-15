/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 00:53
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.entity.MavenMetadata
import cn.rtast.kmvnrepo.entity.res.ArtifactMetadata
import cn.rtast.kmvnrepo.internalRepositories
import cn.rtast.kmvnrepo.publicRepositories
import cn.rtast.kmvnrepo.util.fromXmlString
import cn.rtast.kmvnrepo.util.readText
import cn.rtast.kmvnrepo.util.rootPathOf
import cn.rtast.kmvnrepo.util.toJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private suspend fun ApplicationCall.serveLatestVersion(repository: String) {
    val path = parameters.getAll("path")!!.joinToString("/")
    val metadata = rootPathOf("$repository/$path/maven-metadata.xml")
        .readText().fromXmlString<MavenMetadata>()
    val response = ArtifactMetadata(metadata.versioning.latest, metadata.versioning.lastUpdated)
    respondText(contentType = ContentType.Application.Json, text = response.toJson())
}

fun Application.configureAPIArtifactsRouting() {
    routing {
        publicRepositories.forEach {
            route("/api/artifacts/versions/latest/${it.name}") {
                get("{path...}") { call.serveLatestVersion(it.name) }
            }
        }

        internalRepositories.forEach {
            route("/api/artifacts/versions/latest/${it.name}") {
                authenticate("maven-common") {
                    get("{path...}") { call.serveLatestVersion(it.name) }
                }
            }
        }
    }
}