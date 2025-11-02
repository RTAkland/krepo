/*
 * Copyright © 2025 RTAkland
 * Date: 11/2/25, 9:59 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.pages.other

import cn.rtast.krepo.components.infoToast
import cn.rtast.krepo.components.setLoginInfo
import cn.rtast.krepo.currentPath
import cn.rtast.krepo.util.string.fromJson
import cn.rtast.rutil.string.decodeToBase64
import dev.fritz2.core.RenderContext
import kotlinx.browser.window
import krepo.entity.AzureLoginSuccess

fun RenderContext.azureSignedPage() {
    h1("has-text-centered") {
        +"Setting up user info..."
    }
    try {
        val data = currentPath.split("?").last().split("&").first().split("=").last()
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