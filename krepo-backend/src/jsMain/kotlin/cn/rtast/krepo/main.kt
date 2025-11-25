/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 22:06
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalTime::class)

package cn.rtast.krepo

import cn.rtast.krepo.components.navbar
import cn.rtast.krepo.components.pageFooter
import cn.rtast.krepo.components.warningToast
import cn.rtast.krepo.entity.GetFrontendConfigResponse
import cn.rtast.krepo.pages.homePage
import cn.rtast.krepo.pages.other.azureSignedPage
import cn.rtast.krepo.pages.other.noPermission
import cn.rtast.krepo.pages.publicContentListingPage
import cn.rtast.krepo.pages.search.searchPage
import cn.rtast.krepo.pages.settings.mavenRepositorySettingPage
import cn.rtast.krepo.pages.settings.settingPage
import cn.rtast.krepo.pages.users.editUserPage
import cn.rtast.krepo.pages.users.newUserPage
import cn.rtast.krepo.pages.users.userManagePage
import cn.rtast.krepo.util.auth
import cn.rtast.krepo.util.customBacked
import cn.rtast.krepo.util.file.LocalStorage
import cn.rtast.krepo.util.httpRequest
import cn.rtast.krepo.util.jsonContentType
import cn.rtast.krepo.util.string.fromJson
import dev.fritz2.core.render
import dev.fritz2.headless.components.toastContainer
import dev.fritz2.headless.foundation.portalRoot
import dev.fritz2.routing.routerOf
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import krepo.BACKEND_VERSION_ROUTE
import krepo.BackendVersions
import krepo.entity.BackendVersion
import krepo.entity.FrontendConfig
import krepo.toBackendVersion
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

val router = routerOf("contents")
var currentPath = ""

val backend get() = customBacked
val coroutineScope = MainScope()
lateinit var frontendConfig: FrontendConfig
lateinit var backendVersion: BackendVersions  // zero is legacy version of backend

fun main() {
    toastContainer("default", "toast-container")
    coroutineScope.launch {
        val tokenExpireTimestamp = LocalStorage.EXPIRED_TIMESTAMP
        if (tokenExpireTimestamp != null) {
            val now = Clock.System.now().epochSeconds
            if (now >= tokenExpireTimestamp) {
                warningToast("The Session has expired.")
                LocalStorage.clearAll()
            }
        }

        backendVersion = try {
            httpRequest(BACKEND_VERSION_ROUTE).acceptJson().jsonContentType().get()
                .body().fromJson<BackendVersion>().version.toBackendVersion()
        } catch (e: Exception) {
            e.printStackTrace()
            BackendVersions.STABLE()
        }

        frontendConfig = httpRequest(backendVersion.FRONTEND_CONFIG)
            .auth().acceptJson()
            .jsonContentType().get()
            .body().fromJson<GetFrontendConfigResponse>().data
        document.title = frontendConfig.pageTitle
        render("#target") {
            navbar()
            main {
                inlineStyle("flex-grow: 1;")
                router.data.render { site ->
                    currentPath = site
                    if (site.startsWith("/user/edit")) editUserPage()
                    else if (site.startsWith("/search")) searchPage()
                    else if (site.startsWith("/azure/signed")) azureSignedPage()
                    else {
                        when (site) {
                            "/", "contents" -> homePage()
                            "/user/manage" -> userManagePage()
                            "/user/create" -> newUserPage()
                            "/setting" -> settingPage()
                            "/setting/repository" -> mavenRepositorySettingPage()
                            "/accessDenied" -> noPermission()
                            else -> publicContentListingPage()
                        }
                    }
                }
            }
            pageFooter()
            portalRoot()
        }
    }
}