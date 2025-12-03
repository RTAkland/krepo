/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/29 17:40
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api.system

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.resources.resources
import krepo.azure.util.respondProtobuf
import krepo.azure.util.string.castMarkdownToHtml
import krepo.entity.system.info.AppInfoContent

@HttpRouting("/api/azure/system/privacy")
fun privacyRouting(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    val content: String by resources("info/privacy-en-us.md")
    return request.respondProtobuf(AppInfoContent(content.castMarkdownToHtml()))
}

@HttpRouting("/api/azure/system/terms")
fun termsOfServiceRouting(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    val content: String by resources("info/terms-of-service-en-us.md")
    return request.respondProtobuf(AppInfoContent(content.castMarkdownToHtml()))
}

@HttpRouting("/api/azure/system/licenses")
fun licensesRouting(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    val content: String by resources("info/license-en-us.md")
    return request.respondProtobuf(AppInfoContent(content.castMarkdownToHtml()))
}