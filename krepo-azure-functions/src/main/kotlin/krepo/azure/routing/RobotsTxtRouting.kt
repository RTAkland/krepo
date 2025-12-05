/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/5 08:29
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.response.respondText
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.resources.resources

@HttpRouting("/robots.txt")
fun robotsTxtRouting(
    request: HttpRequest<*>,
    context: HttpContext
) : HttpResponse {
    val content: String by resources("config/robots.txt")
    return request.respondText(content, headers = mapOf("Content-Type" to "text/plain"))
}