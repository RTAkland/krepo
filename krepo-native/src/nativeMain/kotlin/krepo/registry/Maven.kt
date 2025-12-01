/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:41 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(InternalAPI::class)

package krepo.registry

import krepo.client
import krepo.configManager
import krepo.enums.DeployStatus
import krepo.repositories
import krepo.util.file.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.encodeBase64
import io.ktor.utils.io.*
import kotlinx.io.files.Path
import krepo.entity.maven.MirrorRepository

private val validatedArtifactExtension = listOf("klib", "jar", "aar")

private val mavenMetadataFilenames = listOf(
    "maven-metadata.xml",
    "maven-metadata.xml.md5",
    "maven-metadata.xml.sha1",
    "maven-metadata.xml.sha256",
    "maven-metadata.xml.sha512",
)

suspend fun deployMavenArtifact(path: String, channel: ByteReadChannel): DeployStatus {
    val splitPath = path.split("/")
    val filename = splitPath.last()
    val version = splitPath.dropLast(1).last()
    val repositoryName = splitPath.first()
    val repositoryConfig = repositories.find { it.name == repositoryName } ?: return DeployStatus.NotFound
    val fileExtension = filename.split(".").last()
    if (fileExtension in validatedArtifactExtension) {
        if (version.endsWith("-SNAPSHOT") && !repositoryConfig.allowSnapshot) {
            return DeployStatus.NotAllowSNAPSHOT
        }
    }
    val finalDir = rootPathOf(splitPath.dropLast(1).joinToString("/"))
    if (!finalDir.exists()) finalDir.mkdirs()
    val finalFile = Path(finalDir, filename)
    if (!finalFile.exists() || filename in mavenMetadataFilenames) {
        finalFile.writeStreamToFile(channel)
    } else {
        if (!configManager.getConfig().allowRedeploy) return DeployStatus.Conflict
    }
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
                val auth = "${it.username}:${it.password}".encodeBase64()
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
