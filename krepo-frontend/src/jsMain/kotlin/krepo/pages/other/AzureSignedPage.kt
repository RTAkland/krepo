/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:31 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.pages.other

import dev.fritz2.core.RenderContext
import kotlinx.browser.window
import krepo.components.infoToast
import krepo.components.setLoginInfo
import krepo.currentPath
import krepo.entity.user.oauth.AzureLoginSuccess
import krepo.util.fromProtobuf
import krepo.util.string.decodeToByteArray
import krepo.util.string.extractQueryParams

fun RenderContext.AzureSignedPage() {
    h1("has-text-centered") {
        +"Setting up user info..."
    }
    try {
        val data = extractQueryParams(currentPath)["d"]!!
            .decodeToByteArray().fromProtobuf<AzureLoginSuccess>()
        setLoginInfo(data.token, data.name, data.expire, data.email, data.avatarMd5)
        infoToast("Logged in")
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        window.location.href = "/#/"
        window.location.reload()
    }
}