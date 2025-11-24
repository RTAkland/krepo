/*
 * Copyright © 2025 RTAkland
 * Date: 11/23/25, 2:56 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package cn.rtast.krepo.azure.routing.api.v2

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.auth.credentials.BearerCredential
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.krepo.azure.entity.res.FileEntry
import cn.rtast.krepo.azure.entity.res.RepositoryListing
import cn.rtast.krepo.azure.routing.auth.KRepoTokenAuthProvider
import cn.rtast.krepo.azure.util.listFiles
import cn.rtast.krepo.azure.util.unAuth

private fun getFiles(repo: String?, path: String?): List<FileEntry> {
    val p= if (path == "/" || path == ".") "" else path
    return listFiles("$repo/$p")
}

@HttpRouting("api/v2/repository/public/contents")
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
@HttpRouting("api/v2/repository/private/contents")
fun listPrivateRepositoryContent(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    val repository = request.queryParameters["repo"]
    val path = request.queryParameters["path"]
    val files = getFiles(repository, path)
    return request.respondJson(RepositoryListing(200, files.size, files))
}