/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 5:59 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package cn.rtast.krepo.azure.routing

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.Param
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.krepo.azure.registry.handleRequest
import cn.rtast.krepo.azure.routing.auth.KRepoBasicAuthProvider
import com.microsoft.azure.functions.HttpMethod
import java.util.Optional


@AuthConsumer(KRepoBasicAuthProvider::class)
@HttpRouting("releases/{*path}", methods = [HttpMethod.GET, HttpMethod.PUT])
fun deployArtifactRoutingReleases(
    request: HttpRequest<Optional<ByteArray>>,
    context: HttpContext,
    @Param("path") path: String,
): HttpResponse {
    return handleRequest(request, "releases", path, context.logger)
}

@AuthConsumer(KRepoBasicAuthProvider::class)
@HttpRouting("snapshots/{*path}", methods = [HttpMethod.GET, HttpMethod.PUT])
fun deployArtifactRoutingSnapshots(
    request: HttpRequest<Optional<ByteArray>>,
    context: HttpContext,
    @Param("path") path: String,
): HttpResponse {
    return handleRequest(request, "snapshots", path, context.logger)
}

@AuthConsumer(KRepoBasicAuthProvider::class)
@HttpRouting("private/{*path}", methods = [HttpMethod.GET, HttpMethod.PUT])
fun deployArtifactRoutingPrivate(
    request: HttpRequest<Optional<ByteArray>>,
    context: HttpContext,
    @Param("path") path: String,
): HttpResponse {
    return handleRequest(request, "private", path, context.logger)
}




