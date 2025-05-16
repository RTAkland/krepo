/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 02:30
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(InternalAPI::class)

package cn.rtast.kmvnrepo.registry

import cn.rtast.kmvnrepo.client
import cn.rtast.kmvnrepo.configManager
import cn.rtast.kmvnrepo.entity.MirrorRepository
import cn.rtast.kmvnrepo.enums.DeployStatus
import cn.rtast.kmvnrepo.repositories
import cn.rtast.kmvnrepo.util.file.*
import cn.rtast.kmvnrepo.util.string.encodeToBase64
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.utils.io.*
import kotlinx.io.files.Path

private val validatedArtifactExtension = listOf("klib", "jar", "aar")


suspend fun deployMavenArtifact(path: String, channel: ByteReadChannel): DeployStatus {
    val splitPath = path.split("/")
    val filename = splitPath.last()
    val dir = splitPath.dropLast(1).joinToString("/")
    val targetDir = rootPathOf(dir).apply { mkdirs() }
    val targetFile = Path(targetDir, filename)
    val fileExtension = targetFile.toString().split(".").last()
    val repositoryConfig = repositories.find { it.name == splitPath.first() } ?: return DeployStatus.NotFound
    if (targetFile.exists() &&
        fileExtension in repositoryConfig.acceptExtensions &&
        !configManager.getConfig().allowRedeploy
    ) return DeployStatus.Conflict else {
        if (fileExtension in validatedArtifactExtension) {
            val version = splitPath.dropLast(1).last()
            if (version.endsWith("-SNAPSHOT") && !repositoryConfig.allowSnapshot) {
                return DeployStatus.NotAllowSNAPSHOT
            }
        }
        targetFile.writeStreamToFile(channel)
        return DeployStatus.Success
    }
}


suspend fun ApplicationCall.getProxiedArtifacts(
    repository: List<MirrorRepository>,
    path: String,
    originRepository: String
) {
    for (repo in repository) {
        val response = client.get(repo.url.removeSuffix("/") + "/$path") {
            repo.credential?.let {
                val auth = "${it.username}:${it.password}".encodeToBase64()
                header("Authorization", "Basic $auth")
            }
        }
        if (response.status == HttpStatusCode.OK) {
            val channel = response.bodyAsChannel()
            val localPath = "$originRepository/${path.removePrefix("/")}".toPath()
            if (!localPath.exists()) deployMavenArtifact(localPath.toString(), channel)
            this.respondSource(rootPathOf(localPath.toString()).rawSource())
            return
        }
    }
    this.respond(HttpStatusCode.NotFound)
}
