/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:40 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalUuidApi::class, ExperimentalStdlibApi::class)

package krepo.routing.api.oauth

import krepo.client
import krepo.configManager
import krepo.entity.oauth.AzureAccessToken
import krepo.tokenManager
import krepo.userManager
import krepo.util.respondJson
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.encodeBase64
import krepo.entity.user.oauth.AzureLoginSuccess
import krepo.entity.user.oauth.AzureSignInURL
import krepo.entity.user.oauth.AzureSignResponse
import krepo.entity.user.oauth.AzureUserInfo
import krepo.util.fromJson
import krepo.util.toJson
import org.kotlincrypto.hash.md.MD5
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


private val states = mutableListOf<String>()


private val AZURE_LOGIN_URL =
    "https://login.microsoftonline.com/common/oauth2/v2.0/authorize?client_id=${configManager.getConfig().auth.azureClientID}&scope=openid%20email%20profile%20User.Read&redirect_uri=${configManager.getConfig().auth.redirectURI}&response_type=code&state="

private const val REDEEM_TOKEN_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token"
private const val USER_INFO_URL = "https://graph.microsoft.com/v1.0/me"

fun Application.configureAzureSignInRouting() {
    routing {
        get("/api/signin/azure") {
            val code = call.parameters["code"]
            val state = call.parameters["state"]
            try {
                if (code == null || state == null) {
                    call.respondJson(AzureSignResponse(-1, null, null))
                } else {
                    if (!states.contains(state)) {
                        call.respondJson(AzureSignResponse(0, null, null))
                    } else {
                        val msAccessToken = client.submitForm(
                            url = REDEEM_TOKEN_URL,
                            formParameters = parameters {
                                append("client_id", configManager.getConfig().auth.azureClientID)
                                append("grant_type", "authorization_code")
                                append("code", code)
                                append("redirect_uri", configManager.getConfig().auth.redirectURI)
                                append("client_secret", configManager.getConfig().auth.azureClientSecret)
                            }
                        ).bodyAsText().fromJson<AzureAccessToken>()
                        val userInfo = client.get(USER_INFO_URL) {
                            headers {
                                append("Authorization", "Bearer ${msAccessToken.accessToken}")
                            }
                        }.bodyAsText().fromJson<AzureUserInfo>()
                        if (userManager.validateAzureUser(userInfo.userPrincipalName)) {
                            val (token, expiredAt) = tokenManager.grant(userInfo.id, 3600)
                            var localUserInfo = userManager.getUserByUID(userInfo.id)
                            if (localUserInfo == null) {
                                userManager.setUserUID(userInfo.userPrincipalName.lowercase(), userInfo.id)
                                localUserInfo = userManager.getUserByUID(userInfo.id)
                            }
                            val respBase64 = AzureLoginSuccess(
                                token, localUserInfo!!.email,
                                localUserInfo.name,
                                expiredAt, MD5().digest(localUserInfo.email.encodeToByteArray()).toHexString()
                            ).toJson().encodeBase64()
                            call.respondRedirect("/#/azure/signed?d=$respBase64")
                        } else {
                            println(msAccessToken.copy(accessToken = "#######"))
                            println(userInfo)
                            call.respondRedirect("/#/accessDenied")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown Error")
            } finally {
                states.remove(state)
            }
        }

        get("/api/signin/azure/url") {
            val uuid = Uuid.random().toString()
            states.add(uuid)
            call.respondJson(AzureSignInURL(AZURE_LOGIN_URL + uuid))
        }

        post("/api/signin/azure/complete") {
            val state = call.parameters["state"]
            if (state != null) {
                call.respond(HttpStatusCode.OK, "OK")
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}