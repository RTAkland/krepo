/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 6:21 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.registry

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.krepo.azure.userManager
import cn.rtast.krepo.azure.util.bytes
import cn.rtast.krepo.azure.util.error
import cn.rtast.krepo.azure.util.getFile
import cn.rtast.krepo.azure.util.notFound
import cn.rtast.krepo.azure.util.ok
import cn.rtast.krepo.azure.util.unAuth
import cn.rtast.krepo.azure.util.uploadFile
import cn.rtast.rutil.string.decodeToBase64
import com.microsoft.azure.functions.HttpMethod
import java.util.Optional
import java.util.logging.Logger

fun handleRequest(
    request: HttpRequest<Optional<ByteArray>>,
    repository: String,
    path: String,
    logger: Logger
): HttpResponse {
    return if (request.httpMethod == HttpMethod.PUT) deployArtifact(request, repository, path, logger)
    else serveArtifact(request, repository, path, logger)
}

fun deployArtifact(
    request: HttpRequest<Optional<ByteArray>>,
    repository: String,
    path: String,
    logger: Logger
): HttpResponse {
    val (username, password) = request.headers["authorization"]?.split(" ")?.last()?.decodeToBase64()?.split(":")
        ?: return request.unAuth()
    if (userManager.validate(username, password)) {

        uploadFile("$repository/$path", request.body.get(), true)
        return request.ok()
    } else request.unAuth()
    return request.error()
}

fun serveArtifact(
    request: HttpRequest<Optional<ByteArray>>,
    repository: String,
    path: String,
    logger: Logger
): HttpResponse {
    val content = getFile("$repository/$path")
    return if (content == null) request.notFound() else request.bytes(content)
}