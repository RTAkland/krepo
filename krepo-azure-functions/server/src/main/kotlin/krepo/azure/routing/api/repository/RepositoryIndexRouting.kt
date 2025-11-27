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
import cn.rtast.kazure.response.respondBytes
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import krepo.azure.entity.res.CommonResponse
import krepo.azure.routing.auth.KRepoTokenAuthProvider
import krepo.azure.tokenManager
import krepo.azure.util.repo.index.Indexer
import krepo.index.IndexRepositoryResponse
import krepo.index.IndexSearchResponse
import krepo.index.req.DeleteIndexRequest
import krepo.index.req.RebuildIndexRequest
import krepo.util.fromJson
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * protobuf response
 */
@HttpRouting("api/azure/search")
fun searchArtifactsRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    val keyword = request.queryParameters["k"]
        ?: return request.respond("Keyword is required", HttpStatus.BAD_REQUEST)
    if (keyword.length < 3) return request.respondBytes(
        ProtoBuf.encodeToByteArray(IndexSearchResponse(499, emptyList(), 0)),
        HttpStatus.BAD_REQUEST
    )
    val repo = request.queryParameters["r"]
    if (repo == "private") {
        val cred = __getBearerTokenCredential(request) ?: return request.respondBytes(
            ProtoBuf.encodeToByteArray(
                IndexSearchResponse(401, emptyList(), 0)
            ), HttpStatus.UNAUTHORIZED
        )
        if (!tokenManager.validateToken(cred.token)) return request.respondBytes(
            ProtoBuf.encodeToByteArray(IndexSearchResponse(401, emptyList(), 0)),
            HttpStatus.UNAUTHORIZED
        )
    }
    val searchResult = Indexer.search(keyword, repo)
    val resp = IndexSearchResponse(200, searchResult, searchResult.size)
    val protobuf = ProtoBuf.encodeToByteArray(resp)
    return request.respondBytes(protobuf)
}

context(cred: BearerCredential)
@AuthConsumer(KRepoTokenAuthProvider::class)
@HttpRouting("api/azure/repository/index", methods = [HttpMethod.POST])
fun indexRepositoryRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    val repo = request.body.fromJson<RebuildIndexRequest>().repository
    context.logger.info("Starting index repository $repo")
    val startTime = Clock.System.now().epochSeconds
    try {
        Indexer.indexAll(repo)
        val endTime = Clock.System.now().epochSeconds
        return request.respondJson(IndexRepositoryResponse(true, "Success", startTime, endTime, endTime - startTime))
    } catch (e: Exception) {
        e.printStackTrace()
        context.logger.severe("Failed to index repository $repo ${e.message}")
        return request.respondJson(
            IndexRepositoryResponse(false, e.message ?: "No error message", 0, 0, 0),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}

context(cred: BearerCredential)
@AuthConsumer(KRepoTokenAuthProvider::class)
@HttpRouting("api/azure/repository/index", methods = [HttpMethod.DELETE])
fun deleteRepositoryIndexRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    val repo = request.body.fromJson<DeleteIndexRequest>().repository
    Indexer.deleteIndex(repo)
    return request.respondJson(CommonResponse(200, "OK"))
}