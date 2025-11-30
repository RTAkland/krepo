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
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.resources.resources
import krepo.azure.routing.api.repository.repositories
import krepo.azure.tokenManager
import krepo.azure.util.notImplemented
import krepo.azure.util.string.castMarkdownToHtml
import krepo.entity.ConfigRepositoryWithSize
import krepo.entity.FrontendConfig
import krepo.entity.FrontendConfigResponse
import krepo.entity.RepositoryVisibility
import krepo.util.fromJson


val DEFAULT_FRONTEND_CONFIG
    get() = {
        val res: String by resources("config/frontend.json")
        val rawCfg = res.fromJson<FrontendConfig>()
        val cfg = FrontendConfig(
            rawCfg.pageTitle, rawCfg.icpLicense, rawCfg.description?.castMarkdownToHtml(),
            rawCfg.copyright.castMarkdownToHtml(), rawCfg.enableAzureSignIn
        )
        cfg
    }

@HttpRouting("/api/azure/config/frontend")
fun frontendConfigRouting(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    val token = __getBearerTokenCredential(request)
    val repositories = repositories
        .map { ConfigRepositoryWithSize(it.name, it.visibility, it.acceptExtensions, it.allowSnapshot, it.status, 0) }
        .toMutableList()
    return if (token?.token != null && tokenManager.validate(token.token)) request.respondJson(
        FrontendConfigResponse(DEFAULT_FRONTEND_CONFIG.invoke(), repositories)
    ) else request.respondJson(
        FrontendConfigResponse(DEFAULT_FRONTEND_CONFIG.invoke(), repositories.apply {
            removeAll { it.visibility == RepositoryVisibility.Internal }
        })
    )
}

@HttpRouting("/api/azure/config/reset", [HttpMethod.PUT])
fun resetFrontendConfigRouting(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}

@HttpRouting("/api/azure/config/modify", [HttpMethod.PUT])
fun modifyFrontendConfigRouting(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}