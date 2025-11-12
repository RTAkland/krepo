/*
 * Copyright © 2025 RTAkland
 * Date: 11/11/25, 8:05 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.routing.auth

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.BearerCredential
import cn.rtast.kazure.auth.provider.BearerAuthorizationProvider
import cn.rtast.krepo.azure.tokenManager

object KRepoTokenAuthProvider : BearerAuthorizationProvider {
    override fun verify(
        request: HttpRequest<*>,
        context: HttpContext,
        credential: BearerCredential,
    ): Boolean {
        return tokenManager.validateToken(credential.token)
    }
}