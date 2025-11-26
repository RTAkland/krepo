/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:31 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.pages.other

import cn.rtast.rutil.string.decodeToBase64
import dev.fritz2.core.RenderContext
import kotlinx.browser.window
import krepo.components.infoToast
import krepo.components.setLoginInfo
import krepo.currentPath
import krepo.entity.login.oauth.AzureLoginSuccess
import krepo.util.fromJson
import krepo.util.string.extractQueryParams

fun RenderContext.azureSignedPage() {
    h1("has-text-centered") {
        +"Setting up user info..."
    }
    try {
        val data = extractQueryParams(currentPath)["d"]!!
            .apply { println(this) }
            .decodeToBase64().fromJson<AzureLoginSuccess>()
        setLoginInfo(data.t, data.n, data.ex, data.e)
        infoToast("Logged in")
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        window.location.href = "/#/"
        window.location.reload()
    }
}