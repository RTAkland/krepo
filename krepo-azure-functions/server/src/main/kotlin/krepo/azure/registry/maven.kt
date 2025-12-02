/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:50 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.registry

import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.HttpStatus
import cn.rtast.kazure.response.respond
import cn.rtast.kazure.response.respondBytes
import cn.rtast.kazure.response.respondText
import krepo.azure.util.*
import krepo.azure.util.repo.index.Indexer
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.logging.Logger

fun handleRequest(
    request: HttpRequest<ByteArray?>,
    repository: String,
    path: String,
    logger: Logger,
): HttpResponse {
    return if (request.httpMethod == HttpMethod.PUT) deployArtifact(request, repository, path, logger)
    else serveArtifact(request, repository, path, logger, request.httpMethod)
}

/**
 * all repositories are not allowed to publish
 * exists version artifacts
 */
fun deployArtifact(
    request: HttpRequest<ByteArray?>,
    repository: String,
    path: String,
    logger: Logger,
): HttpResponse {
    val content = request.body!!
    if (uploadFile("$repository/$path", content, false)) {
        if (path.endsWith("maven-metadata.xml")) Indexer.indexOnce(repository, path, content)
        return request.respondText("Ok")
    }
    return request.respondText("Conflict", HttpStatus.CONFLICT)
}

fun serveArtifact(
    request: HttpRequest<ByteArray?>,
    repository: String,
    path: String,
    logger: Logger,
    httpMethod: HttpMethod,
): HttpResponse {
    if (repository == "private" && !request.basicAuthCheck().first) return request.unAuth()
    val clientEtag = request.headers["if-none-match"]
    val ifModifiedSince = request.headers["if-modified-since"]?.let { parseHttpDate(it).toInstant() }
    val content = getFileWithMetadata("$repository/$path", clientEtag, ifModifiedSince)
        ?: return request.notFound()
    val headers = mutableMapOf(
        "ETag" to content.etag,
        "Content-Length" to (content.bytes?.size ?: 0L).toString(),
        "Content-Type" to "application/octet-stream"
    )
    content.lastModified?.let { headers["Last-Modified"] = formatHttpDate(it) }
    return when {
        content.matched -> request.respond(null, HttpStatus.NOT_MODIFIED, headers = headers)
        httpMethod == HttpMethod.HEAD -> request.respond(null, HttpStatus.OK, headers = headers)
        else -> request.respondBytes(content.bytes!!, headers = headers)
    }
}

private fun parseHttpDate(value: String): Date =
    SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).parse(value)

private fun formatHttpDate(instant: Instant): String =
    SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }.format(Date.from(instant))