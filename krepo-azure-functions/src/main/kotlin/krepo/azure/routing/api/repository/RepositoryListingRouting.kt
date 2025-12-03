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
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.routing.auth.KRepoTokenAuthProvider
import krepo.azure.util.respondProtobuf
import krepo.entity.maven.ConfigRepositoryWithSize
import krepo.entity.maven.GetRepositoriesResponse
import krepo.entity.maven.RepositoryVisibility
import krepo.enums.RepositoryStatus

/**
 * Accept all file type
 */

internal val repositories = listOf(
    ConfigRepositoryWithSize(
        "releases",
        RepositoryVisibility.Public,
        RepositoryStatus.Available,
        0L
    ),
    ConfigRepositoryWithSize(
        "snapshots",
        RepositoryVisibility.Public,
        RepositoryStatus.Available,
        0L
    ),
    ConfigRepositoryWithSize(
        "private",
        RepositoryVisibility.Internal,
        RepositoryStatus.Available,
        0L
    ),
)

@HttpRouting("/api/azure/repository/public")
fun listPublicRepositories(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse = request.respondProtobuf(
    GetRepositoriesResponse(repositories.toMutableList().apply {
        removeAll { it.visibility == RepositoryVisibility.Internal }
    })
)

context(cred: BearerCredential)
@AuthConsumer(KRepoTokenAuthProvider::class)
@HttpRouting("/api/azure/repository/all")
fun listAllRepositories(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    return request.respondProtobuf(GetRepositoriesResponse(repositories))
}

