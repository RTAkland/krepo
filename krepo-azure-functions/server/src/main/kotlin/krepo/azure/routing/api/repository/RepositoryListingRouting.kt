/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:57 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api.repository

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.auth.credentials.BearerCredential
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.entity.res.CommonResponse
import krepo.azure.entity.res.ConfigRepository
import krepo.azure.entity.res.RepositoryVisibility
import krepo.azure.routing.auth.KRepoTokenAuthProvider
import krepo.enums.RepositoryStatus

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

@HttpRouting("/api/azure/repository/public")
fun listPublicRepositories(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse = request.respondJson(
    CommonResponse(200, repositories.toMutableList().apply {
        removeAll { it.visibility == RepositoryVisibility.Internal }
    })
)

context(cred: BearerCredential)
@AuthConsumer(KRepoTokenAuthProvider::class)
@HttpRouting("/api/azure/repository/all")
fun listAllRepositories(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse = request.respondJson(CommonResponse(200, repositories))

