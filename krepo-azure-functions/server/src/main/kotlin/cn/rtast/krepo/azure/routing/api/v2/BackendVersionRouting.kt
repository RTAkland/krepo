/*
 * Copyright © 2025 RTAkland
 * Date: 11/23/25, 2:13 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package cn.rtast.krepo.azure.routing.api.v2

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.krepo.azure.BACKEND_VERSION

@HttpRouting("api/version")
fun backendVersionRouting(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    return request.respondJson(mapOf("v" to BACKEND_VERSION))
}