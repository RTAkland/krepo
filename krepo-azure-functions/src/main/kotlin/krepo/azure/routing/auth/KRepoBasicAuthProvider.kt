/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:46 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.routing.auth

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.auth.provider.BasicAuthorizationProvider
import krepo.azure.userManager
import krepo.azure.util.Jwt

object KRepoBasicAuthProvider : BasicAuthorizationProvider {
    override fun verify(
        request: HttpRequest<*>,
        context: HttpContext,
        credential: BasicCredential,
    ): Boolean {
        val userValidate = userManager.validate(credential.username, credential.password)
        return if (userValidate) true else Jwt.verify(credential.password, Jwt.TokenPurpose.PUBLISH) != null
    }
}