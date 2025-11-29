/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:57 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api.auth

import cn.rtast.kazure.*
import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.response.*
import cn.rtast.kazure.trigger.HttpRouting
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import krepo.azure.cfg.ConfigManger
import krepo.azure.entity.internal.AzureAccessToken
import krepo.azure.storageManager
import krepo.azure.tokenManager
import krepo.azure.userManager
import krepo.azure.util.client
import krepo.azure.util.hex.md5
import krepo.azure.util.string.decodeToString
import krepo.azure.util.string.encodeToBase64
import krepo.entity.user.oauth.*
import krepo.util.fromJson
import krepo.util.toJson
import java.util.*

private val AZURE_LOGIN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize" +
        "?client_id=${ConfigManger.AZURE_CLIENT_ID}&scope=openid%20email%20profile%20User.Read" +
        "&redirect_uri=${ConfigManger.AZURE_REDIRECT_URL}&response_type=code&state="

private const val REDEEM_TOKEN_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token"
private const val USER_INFO_URL = "https://graph.microsoft.com/v1.0/me"

private fun getState(state: String): String? {
    return storageManager.getKV("krepo_state_$state")
}

private fun addState(state: String, extraData: String) {
    storageManager.setKV("krepo_state_$state", extraData, 120)
}

@HttpRouting("/api/azure/signin/azure/url")
fun azureSignInURLRouting(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    val host = request.queryParameters["host"]
        ?: return request.respond("Forbidden", status = HttpStatus.FORBIDDEN)
    val uuid = UUID.randomUUID().toString().replace("-", "").substring(10)
    addState(uuid, host)
    return request.respondJson(AzureSignInURL(AZURE_LOGIN_URL + uuid))
}

/**
 * remove state from kv or redis ~optional
 * kv has ttl
 */
@HttpRouting("/api/azure/signin/azure/complete", methods = [HttpMethod.POST])
fun azureSignInCompleteRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    val state = request.body.fromJson<AzurePostComplete>().state
    return if (getState(state) != null) {
        storageManager.removeKV("krepo_state_$state")
        request.respondText("OK")
    } else request.respondText("Forbidden", status = HttpStatus.FORBIDDEN)
}


@HttpRouting("/api/azure/signin/azure")
fun azureSignInRouting(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse = runBlocking res@{
    try {
        val code = request.queryParameters["code"]
        val state = request.queryParameters["state"]
        if (code == null || state == null) return@res request.respondJson(AzureSignResponse(-1, null, null))
        val stateAndFrontendHost = getState(state) ?: return@res request.respondJson(AzureSignResponse(0, null, null))
        val frontend = stateAndFrontendHost.decodeToString()
        val msAccessToken = client.submitForm(
            url = REDEEM_TOKEN_URL,
            formParameters = parameters {
                append("code", code)
                append("client_id", ConfigManger.AZURE_CLIENT_ID)
                append("grant_type", "authorization_code")
                append("redirect_uri", ConfigManger.AZURE_REDIRECT_URL)
                append("client_secret", ConfigManger.AZURE_CLIENT_SECRET)
            }
        ).bodyAsText().fromJson<AzureAccessToken>()
        val userInfo = client.get(USER_INFO_URL) {
            headers { append("Authorization", "Bearer ${msAccessToken.accessToken}") }
        }.bodyAsText().fromJson<AzureUserInfo>()
        if (userManager.validateAzureUser(userInfo.userPrincipalName)) {
            val (_, token, expireAt) = tokenManager.oauthIssue(userInfo.id)
            val localUserInfo = userManager.getUserByUID(userInfo.id)!!
            val respBase64 = AzureLoginSuccess(
                token, localUserInfo.email,
                localUserInfo.name, expireAt,
                localUserInfo.email.md5
            ).toJson().encodeToBase64()
            return@res request.respondRedirect(
                "$frontend/#/azure/signed?d=$respBase64",
                redirectType = RedirectType.TEMPORARY_REDIRECT
            )
        } else {
            return@res request.respondRedirect("/#/accessDenied", redirectType = RedirectType.TEMPORARY_REDIRECT)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@res request.respond("Internal Server Error ${e.message}", status = HttpStatus.INTERNAL_SERVER_ERROR)
    }
}