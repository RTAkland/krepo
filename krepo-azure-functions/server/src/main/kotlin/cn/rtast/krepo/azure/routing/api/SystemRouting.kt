/*
 * Copyright © 2025 RTAkland
 * Date: 11/11/25, 9:02 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.routing.api

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.auth.credentials.BearerCredential
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.krepo.azure.entity.res.IsSessionValid
import cn.rtast.krepo.azure.routing.auth.KRepoTokenAuthProvider
import cn.rtast.krepo.azure.tokenManager
import com.microsoft.azure.functions.HttpMethod

context(cred: BearerCredential)
@HttpRouting("api/session", methods = [HttpMethod.OPTIONS])
@AuthConsumer(KRepoTokenAuthProvider::class)
fun isTokenValidRouting(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    val us = tokenManager.getUser(cred.token)
    val res = IsSessionValid(us != null)
    return request.respondJson(res)
}