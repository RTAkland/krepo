/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/29 19:02
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api.system

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.provider.func.__getBearerTokenCredential
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.resources.resources
import krepo.azure.routing.api.repository.repositories
import krepo.azure.tokenManager
import krepo.azure.util.notImplemented
import krepo.azure.util.respondProtobuf
import krepo.azure.util.string.castMarkdownToHtml
import krepo.azure.util.string.fromJson
import krepo.entity.BackendVersion
import krepo.entity.FrontendConfig
import krepo.entity.FrontendConfigResponse
import krepo.entity.maven.RepositoryVisibility


val DEFAULT_FRONTEND_CONFIG
    get() = {
        val res: String by resources("config/frontend.json")
        val rawCfg = res.fromJson<FrontendConfig>()
        val cfg = FrontendConfig(
            rawCfg.pageTitle, rawCfg.icpLicense, rawCfg.description?.castMarkdownToHtml(),
            rawCfg.copyright.castMarkdownToHtml(), rawCfg.enableAzureSignIn,
            BackendVersion(1, -1)  // 1 -> Azure backend schema, -1 -> removed patch version
        )
        cfg
    }

/**
 * frontend config has merged into backend version api endpoint
 * @see krepo.azure.routing.api.backendVersionRouting
 */
@HttpRouting("/api/azure/config/frontend")
fun frontendConfigRouting(
    request: HttpRequest<ByteArray?>,
    context: HttpContext,
): HttpResponse {
    val token = __getBearerTokenCredential(request)
    val repositories = repositories
        .toMutableList()
    return if (token?.token != null && tokenManager.validate(token.token)) request.respondProtobuf(
        FrontendConfigResponse(DEFAULT_FRONTEND_CONFIG.invoke(), repositories)
    ) else request.respondProtobuf(
        FrontendConfigResponse(DEFAULT_FRONTEND_CONFIG.invoke(), repositories.apply {
            removeAll { it.visibility == RepositoryVisibility.Internal }
        })
    )
}

@HttpRouting("/api/azure/config/reset", [HttpMethod.PUT])
fun resetFrontendConfigRouting(
    request: HttpRequest<ByteArray?>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}

@HttpRouting("/api/azure/config/modify", [HttpMethod.PUT])
fun modifyFrontendConfigRouting(
    request: HttpRequest<ByteArray?>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}