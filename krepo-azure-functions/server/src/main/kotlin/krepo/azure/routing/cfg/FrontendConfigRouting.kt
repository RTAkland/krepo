/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:20 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.routing.cfg

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.kazure.util.toJson
import krepo.azure.entity.res.CommonResponse
import krepo.azure.util.CFKV
import krepo.entity.FrontendConfig

val DEFAULT_FRONTEND_CONFIG = FrontendConfig(
    "Maven Repo of RTAST",
    "皖ICP备2024066235-1号",
    "这里是RTAST的Maven仓库!",
    "© #YEAR#  Made by  <a href=\"https://github.com/RTAkland\" target=\"_blank\" title=\"Go to GitHub\">RTAkland</a>",
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