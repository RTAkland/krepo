/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/9 22:52
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util

import cn.rtast.kmvnrepo.client
import cn.rtast.kmvnrepo.entity.ProxiedRepository
import cn.rtast.kmvnrepo.registry.deployMavenArtifact
import cn.rtast.kmvnrepo.util.string.encodeToBase64
import cn.rtast.kmvnrepo.util.string.exists
import cn.rtast.kmvnrepo.util.string.toPath
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.getProxiedArtifacts(
    repository: List<ProxiedRepository>,
    path: String,
    originRepository: String
) {
    try {
        for (repo in repository) {
            val response = client.get(repo.url.removeSuffix("/") + "/$path") {
                repo.credential?.let {
                    val auth = "${it.username}:${it.password}".encodeToBase64()
                    header("Authorization", "Basic $auth")
                }
            }
            if (response.status == HttpStatusCode.OK) {
                val bytes = response.bodyAsBytes()
                val localPath = "$originRepository/${path.removePrefix("/")}".toPath()
                if (!localPath.exists()) deployMavenArtifact(localPath.toString(), bytes)
                this.respondBytes(bytes)
                return
            }
        }
        this.respond(HttpStatusCode.NotFound)
    } catch (_: Exception) {
        this.respond(HttpStatusCode.NotFound)
    }
}