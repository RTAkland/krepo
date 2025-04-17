/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 13:59
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing

import cn.rtast.klogging.KLogging
import cn.rtast.klogging.LogLevel
import cn.rtast.kmvnrepo.enums.DeployStatus
import cn.rtast.kmvnrepo.registry.deployMavenArtifact
import cn.rtast.kmvnrepo.repositories
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val logger = KLogging.getLogger("Deploy").apply { setLoggingLevel(LogLevel.DEBUG) }

fun Application.configureUploadArtifactRouting() {
    routing {
        repositories.forEach {
            authenticate("maven-common") {
                route(it.name) {
                    put("{path...}") {
                        val path = call.request.uri.drop(1)
                        val bytes = call.receive<ByteArray>()
                        val result = deployMavenArtifact(path, bytes)
                        when (result) {
                            DeployStatus.Success -> {
                                logger.debug("Deploy success: $path")
                                call.respond(status = HttpStatusCode.Created, "Success!")
                            }

                            DeployStatus.Conflict -> {
                                logger.debug("Deploy failed: $path conflict")
                                call.respond(status = HttpStatusCode.Conflict, "Conflict!")
                            }

                            DeployStatus.NotAllowSNAPSHOT -> {
                                logger.debug("Deploy failed: Repository(${it.name}) not allow snapshot version")
                                call.respond(
                                    status = HttpStatusCode.BadRequest,
                                    "Repository NOT ALLOW Snapshot version"
                                )
                            }

                            DeployStatus.NotFound -> {
                                logger.debug("Deploy failed: Repository(${it.name} not found")
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