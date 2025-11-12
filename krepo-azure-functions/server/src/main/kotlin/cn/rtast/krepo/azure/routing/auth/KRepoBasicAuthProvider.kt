/*
 * Copyright © 2025 RTAkland
 * Date: 11/10/25, 5:20 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.routing.auth

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.auth.provider.BasicAuthorizationProvider
import cn.rtast.kazure.response.respondJson
import cn.rtast.krepo.azure.tokenManager
import cn.rtast.krepo.azure.userManager

object KRepoBasicAuthProvider : BasicAuthorizationProvider {
    override fun verify(
        request: HttpRequest<*>,
        context: HttpContext,
        credential: BasicCredential,
    ): Boolean {
        return userManager.validate(credential.username, credential.password)
    }
}