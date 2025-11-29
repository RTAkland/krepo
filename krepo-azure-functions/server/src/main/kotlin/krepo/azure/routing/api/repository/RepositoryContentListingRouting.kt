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
import krepo.azure.entity.res.FileEntry
import krepo.azure.entity.res.RepositoryListing
import krepo.azure.routing.auth.KRepoTokenAuthProvider
import krepo.azure.util.listFiles
import krepo.azure.util.unAuth

private fun getFiles(repo: String?, path: String?): List<FileEntry> {
    val p= if (path == "/" || path == "." || path == null) "" else path
    return listFiles("$repo/$p", "/")
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
    return request.respondJson(RepositoryListing(200, files.size, files))
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
    return request.respondJson(RepositoryListing(200, files.size, files))
}