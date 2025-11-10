/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 3:05 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package cn.rtast.krepo.azure.routing.api

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.krepo.azure.routing.auth.KRepoBasicAuthProvider
import cn.rtast.krepo.azure.util.unAuth
import com.microsoft.azure.functions.HttpMethod

@AuthConsumer(KRepoBasicAuthProvider::class)
@HttpRouting("api/login", methods = [HttpMethod.POST])
fun loginRouting(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    // No effect here
    return request.unAuth()
}