/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 22:06
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalTime::class)

package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.components.navbar
import cn.rtast.kmvnrepo.components.pageFooter
import cn.rtast.kmvnrepo.components.warningToast
import cn.rtast.kmvnrepo.entity.Config
import cn.rtast.kmvnrepo.entity.FrontendConfig
import cn.rtast.kmvnrepo.entity.GetFrontendConfigResponse
import cn.rtast.kmvnrepo.pages.homePage
import cn.rtast.kmvnrepo.pages.publicContentListingPage
import cn.rtast.kmvnrepo.pages.search.searchPage
import cn.rtast.kmvnrepo.pages.settings.mavenRepositorySettingPage
import cn.rtast.kmvnrepo.pages.settings.settingPage
import cn.rtast.kmvnrepo.pages.users.editUserPage
import cn.rtast.kmvnrepo.pages.users.newUserPage
import cn.rtast.kmvnrepo.pages.users.userManagePage
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.LocalStorage
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.string.fromJson
import dev.fritz2.core.render
import dev.fritz2.headless.components.toastContainer
import dev.fritz2.headless.foundation.portalRoot
import dev.fritz2.remote.http
import dev.fritz2.routing.routerOf
import kotlinx.browser.document
import kotlinx.browser.window
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