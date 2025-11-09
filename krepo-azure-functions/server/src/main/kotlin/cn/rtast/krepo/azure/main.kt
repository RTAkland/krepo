/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 5:27 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.functions.HttpAzureFunction
import cn.rtast.kazure.respondText
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.krepo.azure.cfg.UserManager
import java.time.LocalDateTime
import java.util.*


val userManager = UserManager()

class IndexRouting : HttpAzureFunction<Optional<String>>() {
    @HttpRouting("/")
    override fun httpEntrypoint(
        request: HttpRequest<Optional<String>>,
        context: HttpContext,
    ): HttpResponse {
        return request.respondText("Ok at ${LocalDateTime.now().second}")
    }
}