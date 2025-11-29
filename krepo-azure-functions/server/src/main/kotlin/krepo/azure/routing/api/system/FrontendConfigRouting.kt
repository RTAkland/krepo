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
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.entity.res.CommonResponse
import krepo.azure.util.notImplemented
import krepo.azure.util.string.castMarkdownToHtml
import krepo.entity.FrontendConfig

val DEFAULT_FRONTEND_CONFIG = FrontendConfig(
    "Maven Repo of RTAST",
    "皖ICP备2024066235-1号",
    "这里是RTAST的Maven仓库!",
    "© #YEAR# Made by <a href=\"https://github.com/RTAkland\" target=\"_blank\" title=\"Go to GitHub\">RTAkland</a>"
        .castMarkdownToHtml(),
    true
)

@HttpRouting("/api/azure/config/frontend")
fun frontendConfigRouting(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    return request.respondJson(CommonResponse(200, DEFAULT_FRONTEND_CONFIG))
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