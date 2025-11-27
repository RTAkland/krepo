/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:50 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.registry

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.response.respondText
import com.microsoft.azure.functions.HttpMethod
import krepo.azure.util.*
import krepo.azure.util.repo.index.Indexer
import java.util.logging.Logger


fun handleRequest(
    request: HttpRequest<ByteArray?>,
    repository: String,
    path: String,
    logger: Logger,
): HttpResponse {
    return if (request.httpMethod == HttpMethod.PUT) deployArtifact(request, repository, path, logger)
    else serveArtifact(request, repository, path, logger)
}

fun deployArtifact(
    request: HttpRequest<ByteArray?>,
    repository: String,
    path: String,
    logger: Logger,
): HttpResponse {
    val content = request.body!!
    if (path.endsWith("maven-metadata.xml")) Indexer.indexOnce(repository, path, content)
    uploadFile("$repository/$path", content, true)
    return request.respondText("Ok")
}

fun serveArtifact(
    request: HttpRequest<ByteArray?>,
    repository: String,
    path: String,
    logger: Logger,
): HttpResponse {
    if (repository == "private") {
        val (ok, _) = request.basicAuthCheck()
        if (!ok) {
            return request.unAuth()
        }
    }
    val content = getFile("$repository/$path")
    return if (content == null) request.notFound() else request.bytes(content)
}