/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:32 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.components

import dev.fritz2.core.*
import dev.fritz2.remote.http
import kotlinx.browser.window
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import krepo.backend
import krepo.backendVersion
import krepo.coroutineScope
import krepo.entity.AzureSignInURL
import krepo.entity.LoginSuccessResponse
import krepo.frontendConfig
import krepo.util.auth
import krepo.util.file.LocalStorage
import krepo.util.file.md5
import krepo.util.fromJson
import krepo.util.httpRequest
import krepo.util.jsonContentType
import krepo.util.string.toBase64

fun RenderContext.navbar() {
    val showLogoutDialog = storeOf(false)
    val showLoginDialog = storeOf(false)
    val username = storeOf("")
    val password = storeOf("")
    val searchKeywordStore = storeOf("")
    nav("navbar level") {
        inlineStyle("background-color: #5181B8")
        inlineStyle("color: #FFFFFF")
        attr("role", "navigation")
        attr("aria-label", "main navigation")
        div("navbar-brand") {
            a("navbar-item is-rounded ml-2") {
                href("#")
                +frontendConfig.pageTitle
                title("Back to home page")
            }
            a("navbar-burger") {
                attr("role", "button")
                attr("aria-label", "menu")
                attr("aria-expanded", "false")
                attr("data-target", "navbarBasic")
                span { attr("aria-hidden", "true") }
                span { attr("aria-hidden", "true") }
                span { attr("aria-hidden", "true") }
            }
        }
        div("navbar-menu") {
            id("navbarBasic")
            div("navbar-start") {
                if (LocalStorage.TOKEN != null) {
                    div("navbar-item has-dropdown is-hoverable") {
                        a("navbar-link") {
                            +"Users"
                        }
                        div("navbar-dropdown") {
                            a("navbar-item") {
                                href("/#/user/manage")
                                +"User list"
                            }
                            a("navbar-item") {
                                href("/#/user/create")
                                +"Create user"
                            }
                        }
                    }
                    div("navbar-item has-dropdown is-hoverable") {
                        a("navbar-link") {
                            +"Settings"
                        }
                        div("navbar-dropdown") {
                            a("navbar-item") {
                                +"Frontend settings"
                                href("/#/setting")
                            }
                            a("navbar-item") {
                                +"Repository settings"
                                href("/#/setting/repository")
                            }
                        }
                    }
                }
                div("navbar-item") {
                    div("control has-icons-left has-icons-right") {
                        input("input is-info is-rounded") {
                            type("text")
                            placeholder("Fill in keyword to search...")
                            changes.values() handledBy searchKeywordStore.update
                            keyups.mapNotNull { it }
                                .filter { it.key == "Enter" }
                                .handledBy {
                                    if (LocalStorage.TOKEN == null) {
                                        warningToast("Please sign in first!")
                                        showLoginDialog.update(true)
                                    } else {
                                        if (searchKeywordStore.current.isBlank()) {
                                            warningToast("Please fill in the keyword to search!")
                                        } else window.location.href = "/#/search?q=${searchKeywordStore.current}"
                                    }
                                }
                        }
                        span("icon is-small is-left") {
                            i("fa-solid fa-magnifying-glass") {}
                        }
                    }
                }
            }
            div("navbar-end") {
                div("navbar-item") {
                    div("buttons") {
                        if (LocalStorage.TOKEN == null) {
                            a("button is-light") {
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
                                title(LocalStorage.CURRENT_USERNAME!!)
                            }
                            span("mr-2") {
                                b {
                                    a {
                                        href("/#/user/edit?username=${LocalStorage.CURRENT_USERNAME!!}")
                                        +LocalStorage.CURRENT_USERNAME!!
                                        title("Update user info")
                                    }
                                }
                            }
                            a("button is-light") {
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
            button("button btn-microsoft-signin") {
                img {
                    src("/assets/img/microsoft.svg")
                    alt("Microsoft logo")
                }
                +"Sign in with Microsoft"

                clicks handledBy {
                    val loginUrl = httpRequest(backendVersion.AZURE_SIGN_IN_URL)
                        .get().body().fromJson<AzureSignInURL>()
                    window.location.href = loginUrl.url
                }
            }
        }
    }, dialogAction = {
        coroutineScope.launch {
            val http = http("${backend}${backendVersion.LOGIN}")
                .header("Authorization", "Basic ${"${username.current}:${password.current}".toBase64()}")
                .acceptJson().jsonContentType().post()
            if (http.ok) {
                val response = http.body().fromJson<LoginSuccessResponse>()
                setLoginInfo(response.token, username.current, response.expiredAt, response.email)
                infoToast("Logged in")
                window.location.reload()
            } else if (http.status == 500) errorToast("Internal server error.") else warningToast("Username or password is incorrect.")
        }
    })

    showDialog(showLogoutDialog, "Logout", "Do you want to logout?", {}) {
        coroutineScope.launch {
            httpRequest(backendVersion.LOGOUT).auth().acceptJson().jsonContentType().post()
            LocalStorage.TOKEN = null
            LocalStorage.CURRENT_USERNAME = null
            LocalStorage.AVATAR = null
            LocalStorage.EMAIL_ADDRESS = null
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
) {
    LocalStorage.TOKEN = token
    LocalStorage.CURRENT_USERNAME = username
    val emailMd5 = md5(email)
    val avatarUrl = "https://gravatar.rtast.cn/avatar/$emailMd5?d=identicon"
    LocalStorage.EMAIL_ADDRESS = email
    LocalStorage.AVATAR = avatarUrl
    LocalStorage.HIDDEN_HASH_FILES = true
    LocalStorage.EXPIRED_TIMESTAMP = expired
}