/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:19 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.routing

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.Param
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.trigger.HttpRouting
import com.microsoft.azure.functions.HttpMethod
import krepo.azure.registry.handleRequest
import krepo.azure.routing.auth.KRepoBasicAuthProvider
import java.util.Optional

@HttpRouting("releases/{*path}", methods = [HttpMethod.GET])
fun serveArtifactRoutingReleases(
    request: HttpRequest<Optional<ByteArray>>,
    context: HttpContext,
    @Param("path") path: String,
): HttpResponse {
    return handleRequest(request, "releases", path, context.logger)
}

@HttpRouting("snapshots/{*path}", methods = [HttpMethod.GET])
fun serveArtifactRoutingSnapshots(
    request: HttpRequest<Optional<ByteArray>>,
    context: HttpContext,
    @Param("path") path: String,
): HttpResponse {
    return handleRequest(request, "snapshots", path, context.logger)
}

@AuthConsumer(KRepoBasicAuthProvider::class)
@HttpRouting("private/{*path}", methods = [HttpMethod.GET])
fun serveArtifactRoutingPrivate(
    request: HttpRequest<Optional<ByteArray>>,
    context: HttpContext,
    @Param("path") path: String,
): HttpResponse {
    return handleRequest(request, "private", path, context.logger)
}

