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
import dev.fritz2.core.storeOf
import dev.fritz2.headless.components.toastContainer
import dev.fritz2.headless.foundation.portalRoot
import dev.fritz2.routing.routerOf
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import krepo.components.NavigatorBar
import krepo.components.PageFooter
import krepo.entity.FrontendConfig
import krepo.entity.FrontendConfigResponse
import krepo.pages.ContentListingPage
import krepo.pages.HomePage
import krepo.pages.info.LicensePage
import krepo.pages.info.PrivacyPage
import krepo.pages.info.TermsPage
import krepo.pages.other.AzureSignedPage
import krepo.pages.other.NoPermission
import krepo.pages.search.SearchPage
import krepo.pages.settings.CommonSettingPage
import krepo.pages.settings.IndexSettingsPage
import krepo.pages.settings.MavenRepositorySettingPage
import krepo.pages.users.NewUserPage
import krepo.pages.users.UserManagePage
import krepo.pages.users.UserSettingsPage
import krepo.util.*
import kotlin.time.ExperimentalTime

val router = routerOf("contents")
var currentPath = ""

val backend get() = customBacked
val coroutineScope = MainScope()
lateinit var frontendConfig: FrontendConfig
lateinit var backendVersion: BackendVersions  // zero is legacy version of backend
lateinit var renderContext: RenderContext
val job = Job(coroutineScope.coroutineContext[Job])
val isDarkTheme = storeOf(false, job)

fun main() {
    val mediaQuery = window.matchMedia("(prefers-color-scheme: dark)")
    if (mediaQuery.matches) isDarkTheme.update(true) else isDarkTheme.update(false)
    toastContainer("default", "toast-container")
    coroutineScope.launchJob {
        val fc = httpRequest(BACKEND_VERSION_ROUTE)
            .auth().acceptJson()
            .jsonContentType().get()
            .body().fromJson<FrontendConfigResponse>()
        frontendConfig = fc.data
        document.title = frontendConfig.pageTitle
        render("#target") {
            NavigatorBar()
            main {
                inlineStyle("flex-grow: 1;")
                router.data.render { site ->
                    renderContext = this
                    currentPath = site
                    if (!site.startsWith("/azure/signed")) checkToken()  // check token if site is not azure signed page
                    if (site.startsWith("/user/edit")) UserSettingsPage()
                    else if (site.startsWith("/search")) SearchPage()
                    else if (site.startsWith("/azure/signed")) AzureSignedPage()
                    else if (site.startsWith("/privacy")) PrivacyPage()
                    else if (site.startsWith("/terms")) TermsPage()
                    else when (site) {
                        "/", "contents" -> HomePage(fc.repositories)
                        "/user/manage" -> UserManagePage()
                        "/user/create" -> NewUserPage()
                        "/setting" -> CommonSettingPage()
                        "/setting/repository" -> MavenRepositorySettingPage()
                        "/setting/index" -> IndexSettingsPage()
                        "/accessDenied" -> NoPermission()
                        "/licenses" -> LicensePage()
                        else -> ContentListingPage()
                    }
                }
            }
            PageFooter()
            portalRoot()
        }
    }
}