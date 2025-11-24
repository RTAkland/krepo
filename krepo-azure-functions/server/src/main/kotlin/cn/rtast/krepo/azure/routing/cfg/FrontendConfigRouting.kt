/*
 * Copyright © 2025 RTAkland
 * Date: 11/12/25, 9:05 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.routing.cfg

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.kazure.util.toJson
import cn.rtast.krepo.azure.entity.res.CommonResponse
import cn.rtast.krepo.azure.entity.res.FrontendConfig
import cn.rtast.krepo.azure.util.CFKV

val DEFAULT_FRONTEND_CONFIG = FrontendConfig(
    "Maven Repo of RTAST",
    "皖ICP备2024066235-1号",
    "这里是RTAST的Maven仓库!",
    "© #YEAR#  Made by  <a href=\"https://github.com/RTAkland\" target=\"_blank\">RTAkland</a>",
    true
)

@HttpRouting("api/azure/config/frontend")
fun frontendConfigRouting(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    val fc = CFKV.getValue("__krepo_fc")
    if (fc == null) {
        CFKV.setValue("__krepo_fc", DEFAULT_FRONTEND_CONFIG.toJson(), -1)
    }
    return request.respondJson(CommonResponse(200, DEFAULT_FRONTEND_CONFIG))
}