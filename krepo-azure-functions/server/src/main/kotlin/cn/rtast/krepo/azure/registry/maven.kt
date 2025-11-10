/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 6:21 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package cn.rtast.krepo.azure.registry

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.response.respondText
import cn.rtast.krepo.azure.util.*
import com.microsoft.azure.functions.HttpMethod
import java.util.*
import java.util.logging.Logger


fun handleRequest(
    request: HttpRequest<Optional<ByteArray>>,
    repository: String,
    path: String,
    logger: Logger,
): HttpResponse {
    return if (request.httpMethod == HttpMethod.PUT) deployArtifact(request, repository, path, logger)
    else serveArtifact(request, repository, path, logger)
}

fun deployArtifact(
    request: HttpRequest<Optional<ByteArray>>,
    repository: String,
    path: String,
    logger: Logger,
): HttpResponse {
    uploadFile("$repository/$path", request.body.get(), true)
    return request.respondText("Ok")
}

fun serveArtifact(
    request: HttpRequest<Optional<ByteArray>>,
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