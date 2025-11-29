/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:57 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api.user

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.auth.credentials.BearerCredential
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.entity.res.CommonResponse
import krepo.azure.routing.auth.KRepoTokenAuthProvider
import krepo.azure.userManager
import krepo.azure.util.notImplemented
import krepo.entity.user.NoSensitiveUser

context(cred: BearerCredential)
@AuthConsumer(KRepoTokenAuthProvider::class)
@HttpRouting("/api/azure/users")
fun usersRouting(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    val users = userManager.allUsers().map { NoSensitiveUser(it.name, it.email) }
    return request.respondJson(CommonResponse(200, users))
}

@HttpRouting("/api/azure/user", methods = [HttpMethod.POST])
fun createUserRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}

@HttpRouting("/api/azure/user/modify", methods = [HttpMethod.PUT])
fun modifyUserRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}