/*
 * Copyright © 2025 RTAkland
 * Date: 11/12/25, 9:15 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.routing.api

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.Param
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.krepo.azure.entity.res.CommonResponse
import cn.rtast.krepo.azure.entity.res.ConfigRepository
import cn.rtast.krepo.azure.entity.res.RepositoryListing
import cn.rtast.krepo.azure.entity.res.RepositoryStatus
import cn.rtast.krepo.azure.entity.res.RepositoryVisibility
import cn.rtast.krepo.azure.util.listFiles

private val defaultAcceptExtensions = listOf(
    "jar", "klib", "aar",
    "so", "a", "dll",
    "dylib", "framework"
)

private val repositories = listOf(
    ConfigRepository(
        "releases",
        RepositoryVisibility.Public,
        defaultAcceptExtensions,
        false,
        RepositoryStatus.Available,
        listOf()
    ),
    ConfigRepository(
        "snapshots",
        RepositoryVisibility.Public,
        defaultAcceptExtensions,
        true,
        RepositoryStatus.Available,
        listOf()
    ),
    ConfigRepository(
        "private",
        RepositoryVisibility.Internal,
        defaultAcceptExtensions,
        true,
        RepositoryStatus.Available,
        listOf()
    ),
)

@HttpRouting("@/api/repositories/public")
fun publicRepositoriesListing(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    return request.respondJson(
        CommonResponse(
            200, repositories.toMutableList().apply {
                removeAll { it.visibility == RepositoryVisibility.Internal }
            })
    )
}

private fun handleRequest(request: HttpRequest<String?>, path: String): HttpResponse {
    val files = listFiles(path)
    val resp = RepositoryListing(200, files.size, files)
    return request.respondJson(resp)
}

@HttpRouting("@/api/contents/{*path}")
fun releasesRepositoriesListing(
    request: HttpRequest<String?>,
    context: HttpContext,
    @Param("path") path: String
): HttpResponse {
    return handleRequest(request, path)
}


//@HttpRouting("@/api/contents/snapshots/{*path}")
//fun snapshotsRepositoriesListing(
//    request: HttpRequest<String?>,
//    context: HttpContext,
//    @Param("path") path: String
//): HttpResponse {
//    return handleRequest(request, path)
//}
//
//context(cred: BearerCredential)
//@AuthConsumer(KRepoTokenAuthProvider::class)
//@HttpRouting("@/api/contents/private/{*path}")
//fun privateRepositoriesListing(
//    request: HttpRequest<String?>,
//    context: HttpContext,
//    @Param("path") path: String
//): HttpResponse {
//    return handleRequest(request, path)
//}
