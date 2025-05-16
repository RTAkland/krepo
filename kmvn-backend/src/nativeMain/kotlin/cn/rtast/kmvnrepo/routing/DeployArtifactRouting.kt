/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 13:59
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing

import cn.rtast.kmvnrepo.enums.DeployStatus
import cn.rtast.kmvnrepo.registry.deployMavenArtifact
import cn.rtast.kmvnrepo.repositories
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureUploadArtifactRouting() {
    routing {
        repositories.forEach {
            authenticate("maven-common") {
                route(it.name) {
                    put("{path...}") {
                        val path = call.request.uri.drop(1)
                        val channel = call.receiveChannel()
                        val result = deployMavenArtifact(path, channel)
                        when (result) {
                            DeployStatus.Success -> {
                                call.respond(status = HttpStatusCode.Created, "Success!")
                            }

                            DeployStatus.Conflict -> {
                                call.respond(status = HttpStatusCode.Conflict, "Conflict!")
                            }

                            DeployStatus.NotAllowSNAPSHOT -> {
                                call.respond(
                                    status = HttpStatusCode.BadRequest,
                                    "Repository NOT ALLOW Snapshot version"
                                )
                            }

                            DeployStatus.NotFound -> {
                                call.respond(
                                    status = HttpStatusCode.NotFound,
                                    "Repository not found"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}