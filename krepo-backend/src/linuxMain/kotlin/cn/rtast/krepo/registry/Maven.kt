/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:54
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(InternalAPI::class)

package cn.rtast.krepo.registry

import cn.rtast.krepo.client
import cn.rtast.krepo.configManager
import cn.rtast.krepo.entity.MirrorRepository
import cn.rtast.krepo.enums.DeployStatus
import cn.rtast.krepo.repositories
import cn.rtast.krepo.util.file.*
import cn.rtast.krepo.util.string.encodeToBase64
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.utils.io.*
import kotlinx.io.files.Path
import platform.posix.rename
import kotlin.random.Random

private val validatedArtifactExtension = listOf("klib", "jar", "aar")


suspend fun deployMavenArtifact(path: String, channel: ByteReadChannel): DeployStatus {
    val tmpDirId = Random.nextInt().toString()
    val splitPath = path.split("/")
    val filename = splitPath.last()
    val version = splitPath.dropLast(1).last()
    val repositoryName = splitPath.first()
    val repositoryConfig = repositories.find { it.name == repositoryName } ?: return DeployStatus.NotFound
    val tmpDir =
        rootPathOf(splitPath.dropLast(1).joinToString("/") + "/.uploading-$tmpDirId").apply { mkdirs() }
    val tmpFile = Path(tmpDir, filename)
    val fileExtension = tmpFile.toString().split(".").last()
    if (fileExtension in validatedArtifactExtension) {
        if (version.endsWith("-SNAPSHOT") && !repositoryConfig.allowSnapshot) {
            return DeployStatus.NotAllowSNAPSHOT
        }
    }
    tmpFile.writeStreamToFile(channel)
    val finalDir = rootPathOf(splitPath.dropLast(1).joinToString("/"))
    if (!finalDir.exists()) finalDir.mkdirs()
    val finalFile = Path(finalDir, filename)
    if (!finalFile.exists()) {
        rename(tmpFile.toString(), finalFile.toString())
    } else {
        if (!configManager.getConfig().allowRedeploy) return DeployStatus.Conflict
        rename(tmpFile.toString(), finalFile.toString())
    }
    if (tmpDir.exists() && tmpDir.listFiles().isEmpty()) tmpDir.delete()
    return DeployStatus.Success
}

suspend fun ApplicationCall.getProxiedArtifacts(
    repository: List<MirrorRepository>,
    path: String,
    originRepository: String,
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
