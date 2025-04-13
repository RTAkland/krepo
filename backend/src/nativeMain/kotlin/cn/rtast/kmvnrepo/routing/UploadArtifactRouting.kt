/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 13:59
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing

import cn.rtast.kmvnrepo.registry.deployMavenArtifact
import cn.rtast.kmvnrepo.repositories
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configureUploadArtifactRouting() {
    repositories.forEach {
        authenticate("maven-common") {
            route(it.name) {
                put("{path...}") {
                    val path = call.request.uri.drop(1)
                    val bytes = call.receive<ByteArray>()
                    val result = deployMavenArtifact(path, bytes)
                    if (result) call.respond(status = HttpStatusCode.Created, "Success!")
                    else call.respond(status = HttpStatusCode.Conflict, "Conflict!")
                }
            }
        }
    }
}