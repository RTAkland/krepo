/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:24 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalTime::class)

package krepo

import dev.fritz2.core.RenderContext
import dev.fritz2.core.render
import dev.fritz2.headless.components.toastContainer
import dev.fritz2.headless.foundation.portalRoot
import dev.fritz2.routing.routerOf
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import krepo.components.navbar
import krepo.components.pageFooter
import krepo.entity.BackendVersion
import krepo.entity.FrontendConfig
import krepo.entity.GetFrontendConfigResponse
import krepo.pages.homePage
import krepo.pages.other.azureSignedPage
import krepo.pages.other.noPermission
import krepo.pages.publicContentListingPage
import krepo.pages.search.searchPage
import krepo.pages.settings.mavenRepositorySettingPage
import krepo.pages.settings.settingPage
import krepo.pages.users.editUserPage
import krepo.pages.users.newUserPage
import krepo.pages.users.userManagePage
import krepo.util.*
import kotlin.time.ExperimentalTime

val router = routerOf("contents")
var currentPath = ""

val backend get() = customBacked
val coroutineScope = MainScope()
lateinit var frontendConfig: FrontendConfig
lateinit var backendVersion: BackendVersions  // zero is legacy version of backend
lateinit var renderContext: RenderContext

fun main() {
    toastContainer("default", "toast-container")
    coroutineScope.launch {
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
                    renderContext = this
                    currentPath = site
                    if (!site.startsWith("/azure/signed")) checkToken()  // check token if site is not azure signed page
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