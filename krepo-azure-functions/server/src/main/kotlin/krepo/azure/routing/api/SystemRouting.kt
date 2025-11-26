/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:47 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.routing.api

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.auth.credentials.BearerCredential
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import com.microsoft.azure.functions.HttpMethod
import krepo.azure.entity.res.IsSessionValid
import krepo.azure.routing.auth.KRepoTokenAuthProvider
import krepo.azure.tokenManager

context(cred: BearerCredential)
@HttpRouting("api/azure/session", methods = [HttpMethod.OPTIONS])
@AuthConsumer(KRepoTokenAuthProvider::class)
fun isTokenValidRouting(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    val us = tokenManager.getUser(cred.token)
    val res = IsSessionValid(us != null)
    return request.respondJson(res)
}