/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:52
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.routing.api

import cn.rtast.krepo.entity.DeleteGavRequest
import cn.rtast.krepo.entity.FileEntry
import cn.rtast.krepo.entity.MavenMetadata
import cn.rtast.krepo.entity.kmp.KotlinToolchainMetadata
import cn.rtast.krepo.entity.res.APIListingResponse
import cn.rtast.krepo.entity.res.ArtifactMetadata
import cn.rtast.krepo.entity.res.ArtifactSearchResponse
import cn.rtast.krepo.entity.res.CommonResponse
import cn.rtast.krepo.enums.KotlinMultiplatformProjectType
import cn.rtast.krepo.internalRepositories
import cn.rtast.krepo.publicRepositories
import cn.rtast.krepo.util.file.*
import cn.rtast.krepo.util.respondJson
import cn.rtast.krepo.util.string.fromJson
import cn.rtast.krepo.util.string.fromXmlString
import cn.rtast.krepo.util.string.toJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.io.files.Path
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import krepo.entity.RepositoryVisibility

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
    klib: Boolean,
) {
    try {
        val artifactPath = rootPathOf("$repo/${group.replace(".", "/")}/$artifact/$version")
        if (klib) {
            val toolchainMetadata = Path(artifactPath, "$artifact-$version-kotlin-tooling-metadata.json")
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
            text = CommonResponse(200, "构件已被删除").toJson()
        )
    } catch (e: Exception) {
        respondText(
            contentType = ContentType.Application.Json,
            text = CommonResponse(-200, "构件删除失败 ${e.message}").toJson()
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
        val result = ArtifactSearchResponse(200, "搜索成功", resultList.size, resultList)
        respondJson(result)
    } catch (e: Exception) {
        respondJson(ArtifactSearchResponse(-200, "搜索失败 ${e.message}", 0, emptyList()))
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