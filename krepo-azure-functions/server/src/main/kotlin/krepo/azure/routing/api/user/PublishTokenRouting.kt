/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/1 20:56
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
import krepo.azure.routing.auth.KRepoTokenAuthProvider
import krepo.azure.tokenManager
import krepo.entity.maven.token.GrantPublishTokenRequest
import krepo.entity.maven.token.GrantPublishTokenResponse
import krepo.util.fromJson

context(cred: BearerCredential)
@AuthConsumer(KRepoTokenAuthProvider::class)
@HttpRouting("/api/azure/publish-credential", [HttpMethod.POST])
fun createUserPublishCredentialRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    val username = request.body.fromJson<GrantPublishTokenRequest>().name
    val publishToken = tokenManager.issuePublish(username)
    val resp = GrantPublishTokenResponse(username, publishToken.value, publishToken.expiredAt)
    return request.respondJson(resp)
}