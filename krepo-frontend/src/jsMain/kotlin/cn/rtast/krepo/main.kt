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
import cn.rtast.krepo.entity.Config
import cn.rtast.krepo.entity.FrontendConfig
import cn.rtast.krepo.entity.GetFrontendConfigResponse
import cn.rtast.krepo.pages.homePage
import cn.rtast.krepo.pages.publicContentListingPage
import cn.rtast.krepo.pages.search.searchPage
import cn.rtast.krepo.pages.settings.mavenRepositorySettingPage
import cn.rtast.krepo.pages.settings.settingPage
import cn.rtast.krepo.pages.users.editUserPage
import cn.rtast.krepo.pages.users.newUserPage
import cn.rtast.krepo.pages.users.userManagePage
import cn.rtast.krepo.util.auth
import cn.rtast.krepo.util.file.LocalStorage
import cn.rtast.krepo.util.httpRequest
import cn.rtast.krepo.util.jsonContentType
import cn.rtast.krepo.util.string.fromJson
import dev.fritz2.core.render
import dev.fritz2.headless.components.toastContainer
import dev.fritz2.headless.foundation.portalRoot
import dev.fritz2.remote.http
import dev.fritz2.routing.routerOf
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

val router = routerOf("contents")
var currentPath = ""
var backend = "http://127.0.0.1:9098"
val coroutineScope = MainScope()
lateinit var frontendConfig: FrontendConfig

fun main() {
    toastContainer("default", "toast-container")
    coroutineScope.launch {
//        if (window.location.hostname != "localhost")
            backend = http("/config.json").get().body().fromJson<Config>().backend
        try {
            val checkSessionValid = http("$backend/@/api/user/")
                .auth().acceptJson().jsonContentType()
                .get()
            if (checkSessionValid.status == 401 && LocalStorage.TOKEN != null) {
                warningToast("Session was expired")
                LocalStorage.clearAll()
            }
        } catch (_: Exception) {
        }
        frontendConfig = httpRequest("/@/api/config/frontend")
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
                    else {
                        when (site) {
                            "/", "contents" -> homePage()
                            "/user/manage" -> userManagePage()
                            "/user/create" -> newUserPage()
                            "/setting" -> settingPage()
                            "/setting/repository" -> mavenRepositorySettingPage()
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