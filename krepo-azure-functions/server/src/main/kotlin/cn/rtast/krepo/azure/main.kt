/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 5:27 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package cn.rtast.krepo.azure

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.response.respondText
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.krepo.azure.cfg.TokenManager
import cn.rtast.krepo.azure.cfg.UserManager
import java.util.*


val userManager = UserManager()
val tokenManager = TokenManager()

@HttpRouting("/")
fun httpEntrypoint(
    request: HttpRequest<Optional<String>>,
    context: HttpContext,
): HttpResponse {
    return request.respondText("Ok at ${Date().time}")
}