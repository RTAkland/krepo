/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 00:53
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.entity.DeleteGavRequest
import cn.rtast.kmvnrepo.entity.FileEntry
import cn.rtast.kmvnrepo.entity.MavenMetadata
import cn.rtast.kmvnrepo.entity.kmp.KotlinToolchainMetadata
import cn.rtast.kmvnrepo.entity.res.APIListingResponse
import cn.rtast.kmvnrepo.entity.res.ArtifactMetadata
import cn.rtast.kmvnrepo.entity.res.ArtifactSearchResponse
import cn.rtast.kmvnrepo.entity.res.CommonResponse
import cn.rtast.kmvnrepo.enums.KotlinMultiplatformProjectType
import cn.rtast.kmvnrepo.enums.RepositoryVisibility
import cn.rtast.kmvnrepo.internalRepositories
import cn.rtast.kmvnrepo.publicRepositories
import cn.rtast.kmvnrepo.util.*
import cn.rtast.kmvnrepo.util.manager.i18n
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.io.files.Path
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private suspend fun ApplicationCall.serveLatestVersion(repository: String) {
    val path = parameters.getAll("path")!!.joinToString("/")
    val metadata = rootPathOf("$repository/$path/maven-metadata.xml")
        .readText().fromXmlString<MavenMetadata>()
    val response = ArtifactMetadata(metadata.versioning.latest, metadata.versioning.lastUpdated)
    respondText(contentType = ContentType.Application.Json, text = response.toJson())
}

private suspend fun ApplicationCall.handleRequest(repository: String) {
    val fullPath = rootPathOf("$repository/${parameters.getAll("path")!!.joinToString("/")}")
    if (!fullPath.exists()) {
        respondText(APIListingResponse(-200, 0, emptyList()).toJson(), ContentType.Application.Json)
        return
    }
    val entries = fullPath.listFiles().map {
        FileEntry(
            name = it.name,
            isDirectory = it.isDirectory(),
            size = if (it.isFile()) it.size() else -1,
            timestamp = it.getFileModifiedTimestamp()
        )
    }
    respondText(APIListingResponse(200, entries.size, entries).toJson(), ContentType.Application.Json)
}

private suspend fun ApplicationCall.deleteArtifact(
    repo: String,
    group: String,
    artifact: String,
    version: String,
    klib: Boolean
) {
    try {
        val artifactPath = rootPathOf("$repo/${group.replace(".", "/")}/$artifact/$version")
        if (klib) {
            val toolchainMetadata = Path(artifactPath, "test-publish-0.0.3-kotlin-tooling-metadata.json")
                .readText().fromJson<KotlinToolchainMetadata>()
            val extraModules = mutableListOf<String>()
            toolchainMetadata.projectTargets.filter { it.platformType != KotlinMultiplatformProjectType.common }
                .forEach {
                    when (it.platformType) {
                        KotlinMultiplatformProjectType.js -> extraModules.add("js")
                        KotlinMultiplatformProjectType.wasm -> extraModules.add("wasm-js")
                        KotlinMultiplatformProjectType.common -> null
                        KotlinMultiplatformProjectType.jvm -> extraModules.add("jvm")
                        KotlinMultiplatformProjectType.native -> {
                            val targetExtension = it.extras!!["native"]
                                ?.jsonObject?.get("konanTarget")
                                ?.jsonPrimitive?.content!!
                            extraModules.add(targetExtension.replace("_", ""))
                        }
                    }
                }
            extraModules.forEach {
                rootPathOf("$repo/${group.replace(".", "/")}/${artifact}-$it/$version").deleteRec()
            }
        }
        artifactPath.deleteRec()
        respondText(
            contentType = ContentType.Application.Json,
            text = CommonResponse(200, i18n("artifact.delete.success")).toJson()
        )
    } catch (e: Exception) {
        respondText(
            contentType = ContentType.Application.Json,
            text = CommonResponse(-200, "${i18n("artifact.delete.failure")} ${e.message}").toJson()
        )
    }
}

private suspend fun RoutingCall.searchArtifacts(repo: String) {
    try {
        val name = queryParameters["name"] ?: return
        val path = rootPathOf(repo)
        val searchResult = path.searchDirectory(name)
        val resultList = mutableListOf<ArtifactSearchResponse.Artifact>()
        searchResult.forEach { result ->
            val versions = result.listFiles().filter { it.isDirectory() }.map { it.name }
            val repository = result.toString().split("/")[2]
            val group = result.toString().split("/").dropLast(1).drop(3)
            val groupCoordinate = group.joinToString(".")
            val groupSlashed = group.joinToString("/")
            val artifactId = result.toString().split("/").last()
            resultList.add(
                ArtifactSearchResponse.Artifact(
                    groupCoordinate, groupSlashed, repository,
                    artifactId, versions
                )
            )
        }
        val result = ArtifactSearchResponse(200, i18n("search.success"), resultList.size, resultList)
        respondJson(result)
    } catch (e: Exception) {
        respondJson(ArtifactSearchResponse(-200, "${i18n("search.failure")} ${e.message}", 0, emptyList()))
    }
}

fun Application.configureAPIArtifactsRouting() {
    routing {
        authenticate("api") {
            delete("/@/api/artifacts") {
                val repository = call.queryParameters["repo"] ?: return@delete
                val group = call.queryParameters["group"] ?: return@delete
                val artifactId = call.queryParameters["artifact"] ?: return@delete
                val version = call.queryParameters["version"] ?: return@delete
                val klib = call.queryParameters["klib"]?.toBoolean() == true
                call.deleteArtifact(repository, group, artifactId, version, klib)
            }

            delete("/@/api/gav") {
                val gav = call.receive<DeleteGavRequest>().gav
                val path = ("repositories/$gav").toPath()
                if (path.cIsDirectory()) path.deleteRec() else path.delete()
                call.respond(HttpStatusCode.OK, CommonResponse(200, "删除成功"))
            }
        }

        (publicRepositories + internalRepositories).forEach { repo ->
            val routeBuilder: Route.() -> Unit = {
                get("/@/api/artifacts/versions/latest/${repo.name}/{path...}") {
                    call.serveLatestVersion(repo.name)
                }
                get("/@/api/contents/${repo.name}/{path...}") {
                    call.handleRequest(repo.name)
                }
                get("/@/api/artifacts/search/${repo.name}") {
                    call.searchArtifacts(repo.name)
                }
            }

            if (repo.visibility == RepositoryVisibility.Internal) {
                authenticate("api", build = routeBuilder)
            } else {
                routeBuilder(this)
            }
        }
    }
}