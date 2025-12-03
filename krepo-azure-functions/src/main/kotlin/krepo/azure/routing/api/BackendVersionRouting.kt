/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/2 15:58
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.routing.api.system.frontendConfigRouting

@HttpRouting("/api/version")
fun backendVersionRouting(
    request: HttpRequest<ByteArray?>,
    context: HttpContext,
): HttpResponse {
    return frontendConfigRouting(request, context)
}