/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:57 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api.repository

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.auth.credentials.BearerCredential
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.routing.auth.KRepoTokenAuthProvider
import krepo.azure.userManager
import krepo.azure.util.listFiles
import krepo.azure.util.unAuth
import krepo.entity.FileEntry
import krepo.entity.RepositoryContents

private fun getFiles(repo: String?, path: String?): List<FileEntry> {
    val p = if (path == "/" || path == "." || path == null) "" else path
    return listFiles("$repo/$p", "/").map {
        val ns = if (p == "") it.name else p
        it.copy(owner = userManager.getNamespaceOwner(ns)?.name)
    }
}

@HttpRouting("/api/azure/repository/public/contents")
fun listPublicRepositoryContent(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    val repository = request.queryParameters["repo"]
    if (repository == "private") return request.unAuth()
    val path = request.queryParameters["path"]
    val files = getFiles(repository, path)
    return request.respondJson(RepositoryContents(200, files.size, files))
}

context(cred: BearerCredential)
@AuthConsumer(KRepoTokenAuthProvider::class)
@HttpRouting("/api/azure/repository/private/contents")
fun listPrivateRepositoryContent(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    val repository = request.queryParameters["repo"]
    val path = request.queryParameters["path"]
    val files = getFiles(repository, path)
    return request.respondJson(RepositoryContents(200, files.size, files))
}