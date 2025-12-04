/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:32 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.components

import dev.fritz2.core.*
import dev.fritz2.remote.http
import kotlinx.browser.window
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import krepo.*
import krepo.util.img.autoFASvg
import krepo.entity.user.LoginSuccessResponse
import krepo.entity.user.oauth.AzureRequestSignInURL
import krepo.entity.user.oauth.AzureSignInURL
import krepo.util.*
import krepo.util.byte.fromProtoBuf
import krepo.util.file.LocalStorage
import krepo.util.string.encodeToBase64

fun RenderContext.NavigatorBar() {
    val showLogoutDialog = storeOf(false)
    val showLoginDialog = storeOf(false)
    val username = storeOf("")
    val password = storeOf("")
    val searchKeywordStore = storeOf("")
    val oauthButtonDisabled = storeOf(false)
    nav("navbar level") {
        inlineStyle("color: #FFFFFF")
        attr("role", "navigation")
        attr("aria-label", "main navigation")
        div("navbar-brand") {
            a("navbar-item is-rounded navbar-app-name-text") {
                +frontendConfig.pageTitle
                title("Back to home page")
                clicks handledBy { navTo("/") }
            }
            a("navbar-burger") {
                attr("role", "button")
                attr("aria-label", "menu")
                attr("aria-expanded", "false")
                attr("data-target", "navbarBasic")
            }
        }
        div("navbar-menu") {
            id("navbarBasic")
            div("navbar-start") {
                if (LocalStorage.TOKEN != null) {
                    div("navbar-item has-dropdown is-hoverable") {
                        a("navbar-link") { +"Users" }
                        div("navbar-dropdown") {
                            a("navbar-item") {
                                +"User list"
                                clicks handledBy { navTo("/#/user/manage") }
                            }
                            a("navbar-item") {
                                +"Create user"
                                clicks handledBy { navTo("/#/user/create") }
                            }
                        }
                    }
                    div("navbar-item has-dropdown is-hoverable") {
                        a("navbar-link") { +"Settings" }
                        div("navbar-dropdown") {
                            a("navbar-item") {
                                +"Frontend settings"
                                clicks handledBy { navTo("/#/setting") }
                            }
                            a("navbar-item") {
                                +"Repository settings"
                                clicks handledBy { navTo("/#/setting/repository") }
                            }
                            a("navbar-item") {
                                +"Index settings"
                                clicks handledBy { navTo("/#/setting/index") }
                            }
                            a("navbar-item") {
                                +"Grant publish token"
                                clicks handledBy { navTo("/#/user/edit", mapOf("username" to LocalStorage.USERNAME)) }
                            }
                        }
                    }
                }
                div("navbar-item") {
                    div("control has-icons-left has-icons-right") {
                        input("input is-rounded") {
                            id("searchInput")
                            type("text")
                            placeholder("Fill in keyword to search...")
                            changes.values() handledBy searchKeywordStore.update
                            keyups.mapNotNull { it }
                                .filter { it.key == "Enter" }
                                .handledBy {
                                    if (searchKeywordStore.current.isBlank()) {
                                        warningToast("Please fill in the keyword to search!")
                                    } else window.location.href = "/#/search?k=${searchKeywordStore.current}"
                                }
                        }
                        span("icon is-small is-left") { autoFASvg("fa-magnifying-glass", "", size = 14) }
                        title("Search artifacts")
                    }
                }
            }
            div("navbar-end") {
                div("navbar-item") {
                    div("buttons") {
                        if (LocalStorage.TOKEN == null) {
                            a("button") {
                                isDarkTheme.data.render { if (!it) this@a.className("is-light") }
                                +"Sign in"
                                clicks handledBy { showLoginDialog.update(true) }
                            }
                        } else {
                            figure("image is-32x26") {
                                img {
                                    src(LocalStorage.AVATAR!!)
                                    attr("alt", "Avatar")
                                    className("is-rounded")
                                }
                                title(LocalStorage.USERNAME!!)
                            }
                            span("mr-2") {
                                b {
                                    a {
                                        href("/#/user/edit?username=${LocalStorage.USERNAME!!}")
                                        +LocalStorage.USERNAME!!
                                        title("Update user info")
                                    }
                                }
                            }
                            a("button") logoutButton@{
                                isDarkTheme.data.render { if (!it) this@logoutButton.className("is-light") }
                                +"Logout"
                                clicks handledBy { showLogoutDialog.update(true) }
                            }
                        }
                    }
                }
            }
        }
    }
    showDialog(showLoginDialog, "Sign in", null, content = {
        div("field") {
            label("label") { +"Username" }
            div("control") {
                input("input") {
                    type("text")
                    placeholder("Fill in username here")
                    value(username.data)
                    changes.values() handledBy username.update
                }
            }
        }
        div("field") {
            label("label") { +"Password" }
            div("control") {
                input("input") {
                    type("password")
                    placeholder("Fill in password here")
                    value(password.data)
                    changes.values() handledBy password.update
                }
            }
        }

    }, extraButtons = {
        if (frontendConfig.enableAzureSignIn) {
            button("button btn-microsoft-signin has-background-black") {
                oauthButtonDisabled.data.render { disabled(it) }
                img {
                    src("/assets/img/microsoft.svg")
                    alt("Microsoft logo")
                }
                +"Sign in with Microsoft"

                clicks handledBy {
                    oauthButtonDisabled.update(true)
                    val body = AzureRequestSignInURL(getCurrentHttpUrl().encodeToBase64(), "azure")
                    val loginUrl = httpRequest(backendVersion.AZURE_SIGN_IN_URL)
                        .setOctetBody(body).post().arrayBuffer().fromProtoBuf<AzureSignInURL>()
                    oauthButtonDisabled.update(false)
                    window.location.href = loginUrl.url
                }
            }
        }
    }, dialogAction = {
        coroutineScope.launchJob {
            val http = http("${backend}${backendVersion.LOGIN}")
                .header("Authorization", "Basic ${"${username.current}:${password.current}".encodeToBase64()}").post()
            if (http.ok) {
                val response = http.arrayBuffer().fromProtoBuf<LoginSuccessResponse>()
                setLoginInfo(
                    response.token, username.current,
                    response.expiredAt, response.email,
                    response.avatarMd5
                )
                infoToast("Logged in")
                window.location.reload()
            } else if (http.status == 500) errorToast("Internal server error.") else warningToast("Username or password is incorrect.")
        }
    })

    showDialog(showLogoutDialog, "Logout", "Do you want to logout?", {}) {
        coroutineScope.launchJob {
            httpRequest(backendVersion.LOGOUT).auth().octetType().post()
            LocalStorage.TOKEN = null
            LocalStorage.USERNAME = null
            LocalStorage.AVATAR = null
            LocalStorage.EMAIL = null
            window.location.href = "/#/"
            window.location.reload()
        }
    }
}


fun setLoginInfo(
    token: String,
    username: String,
    expired: Long,
    email: String,
    md5: String,
) {
    LocalStorage.TOKEN = token
    LocalStorage.USERNAME = username
    LocalStorage.EMAIL = email
    LocalStorage.AVATAR = "https://gravatar.rtast.cn/avatar/${md5}?d=identicon"
    LocalStorage.EXPIRED_TIMESTAMP = expired
}