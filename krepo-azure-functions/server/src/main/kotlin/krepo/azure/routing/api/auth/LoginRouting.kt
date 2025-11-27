/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:47 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api.auth

import cn.rtast.kazure.*
import cn.rtast.kazure.auth.AuthConsumer
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.auth.credentials.BearerCredential
import cn.rtast.kazure.response.respond
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.routing.auth.KRepoBasicAuthProvider
import krepo.azure.routing.auth.KRepoTokenAuthProvider
import krepo.azure.tokenManager
import krepo.azure.userManager
import krepo.azure.util.hex.md5
import krepo.entity.login.LoginSuccessResponse
import java.security.MessageDigest


context(cred: BasicCredential)
@AuthConsumer(KRepoBasicAuthProvider::class)
@HttpRouting("api/azure/login", methods = [HttpMethod.POST])
fun loginRouting(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    val token = tokenManager.issue(cred.username)
    val user = userManager.getUser(cred.username)!!
    val loginRes =
        LoginSuccessResponse(
            200, "Logged in", token.value, user.email,
            cred.username, token.expiredAt, user.email.md5
        )
    return request.respondJson(loginRes)
}

context(cred: BearerCredential)
@AuthConsumer(KRepoTokenAuthProvider::class)
@HttpRouting("api/v2/logout", methods = [HttpMethod.POST])
fun logoutRouting(
    request: HttpRequest<String?>,
    context: HttpContext,
): HttpResponse {
    return request.respond("Ok", status = HttpStatus.OK)
}