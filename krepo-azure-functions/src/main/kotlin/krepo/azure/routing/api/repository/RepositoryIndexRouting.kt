/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 5:00 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalSerializationApi::class, ExperimentalTime::class)
@file:Suppress("unused")

package krepo.azure.routing.api.repository

import cn.rtast.kazure.*
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.auth.credentials.BearerCredential
import cn.rtast.kazure.auth.provider.func.__getBearerTokenCredential
import cn.rtast.kazure.response.respond
import cn.rtast.kazure.trigger.HttpRouting
import kotlinx.serialization.ExperimentalSerializationApi
import krepo.azure.routing.auth.KRepoTokenAuthProvider
import krepo.azure.tokenManager
import krepo.azure.util.repo.index.Indexer
import krepo.azure.util.respondProtobuf
import krepo.entity.CommonResponse
import krepo.index.IndexRepositoryResponse
import krepo.index.IndexSearchResponse
import krepo.index.req.DeleteIndexRequest
import krepo.index.req.RebuildIndexRequest
import krepo.util.fromProtobuf
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@HttpRouting("/api/azure/search")
fun searchArtifactsRouting(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    val keyword = request.queryParameters["k"]?.lowercase()
        ?: return request.respond("Keyword is required", HttpStatus.BAD_REQUEST)
    if (keyword.length < 3) return request.respondProtobuf(
        IndexSearchResponse(499, emptyList(), 0),
        HttpStatus.BAD_REQUEST
    )
    val repo = request.queryParameters["r"]
    if (repo == "private") {
        val cred = __getBearerTokenCredential(request) ?: return request.respondProtobuf(
            IndexSearchResponse(401, emptyList(), 0), HttpStatus.UNAUTHORIZED
        )
        if (!tokenManager.validate(cred.token)) return request.respondProtobuf(
            IndexSearchResponse(401, emptyList(), 0),
            HttpStatus.UNAUTHORIZED
        )
    }
    val searchResult = Indexer.search(keyword, repo)
    val resp = IndexSearchResponse(200, searchResult, searchResult.size)
    return request.respondProtobuf(resp)
}

context(cred: BearerCredential)
@AuthConsumer(KRepoTokenAuthProvider::class)
@HttpRouting("/api/azure/repository/index", methods = [HttpMethod.POST])
fun indexRepositoryRouting(
    request: HttpRequest<ByteArray>,
    context: HttpContext,
): HttpResponse {
    val repo = request.body.fromProtobuf<RebuildIndexRequest>().repository
    context.logger.info("Starting index repository $repo")
    val startTime = Clock.System.now().epochSeconds
    try {
        Indexer.indexAll(repo)
        val endTime = Clock.System.now().epochSeconds
        return request.respondProtobuf(IndexRepositoryResponse(true, "Success", startTime, endTime, endTime - startTime))
    } catch (e: Exception) {
        e.printStackTrace()
        context.logger.severe("Failed to index repository $repo ${e.message}")
        return request.respondProtobuf(
            IndexRepositoryResponse(false, e.message ?: "No error message", 0, 0, 0),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}

context(cred: BearerCredential)
@AuthConsumer(KRepoTokenAuthProvider::class)
@HttpRouting("/api/azure/repository/index", methods = [HttpMethod.DELETE])
fun deleteRepositoryIndexRouting(
    request: HttpRequest<ByteArray>,
    context: HttpContext,
): HttpResponse {
    val repo = request.body.fromProtobuf<DeleteIndexRequest>().repository
    Indexer.deleteIndex(repo)
    return request.respondProtobuf(CommonResponse(200, "OK"))
}