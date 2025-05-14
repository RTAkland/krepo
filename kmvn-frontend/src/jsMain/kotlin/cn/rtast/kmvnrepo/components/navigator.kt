/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 19:46
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.components

import cn.rtast.kmvnrepo.backend
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.entity.LoginSuccessResponse
import cn.rtast.kmvnrepo.frontendConfig
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.LocalStorage
import cn.rtast.kmvnrepo.util.file.md5
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.string.fromJson
import cn.rtast.kmvnrepo.util.string.toBase64
import dev.fritz2.core.*
import dev.fritz2.remote.http
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.navbar() {
    val showLogoutDialog = storeOf(false)
    val showLoginDialog = storeOf(false)
    val username = storeOf("")
    val password = storeOf("")
    val searchQueryStore = storeOf("")
    nav("navbar level") {
        inlineStyle("background-color: #5181B8")
        inlineStyle("color: #FFFFFF")
        attr("role", "navigation")
        attr("aria-label", "main navigation")
        div("navbar-brand") {
            a("navbar-item is-rounded ml-2") {
                href("#")
                +frontendConfig.pageTitle
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
                                +"User List"
                            }
                            a("navbar-item") {
                                href("/#/user/create")
                                +"Create User"
                            }
                        }
                    }
                    div("navbar-item has-dropdown is-hoverable") {
                        a("navbar-link") {
                            +"Settings"
                        }
                        div("navbar-dropdown") {
                            a("navbar-item") {
                                +"Frontend Settings"
                                href("/#/setting")
                            }
                            a("navbar-item") {
                                +"Repository Settings"
                                href("/#/setting/repository")
                            }
                        }
                    }
                }
//                div("navbar-item") {
//                    div("control") {
//                        input("input search-placeholder") {
//                            type("text")
//                            placeholder("Search here...")
//                            changes.values() handledBy searchQueryStore.update
//                        }
//                    }
//                    i("fa-solid fa-search button is-light") {
//                        clicks handledBy {
//                            if (searchQueryStore.current.isBlank()) warningToast("Please fill in the search keyword")
//                            else window.location.href = "/#/search?q=${searchQueryStore.current}"
//                        }
//                    }
//                }
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
                                        title("Update my info")
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
    showDialog(showLoginDialog, "Sign in", null, {
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
    }) {
        coroutineScope.launch {
            val http = http("$backend/@/api/login")
                .header("Authorization", "Basic ${"${username.current}:${password.current}".toBase64()}")
                .acceptJson().jsonContentType().post()
            if (http.ok) {
                val response = http.body().fromJson<LoginSuccessResponse>()
                LocalStorage.TOKEN = response.token
                LocalStorage.CURRENT_USERNAME = username.current
                val emailMd5 = md5(response.email)
                val avatarUrl = "https://gravatar.rtast.cn/avatar/$emailMd5?d=identicon"
                LocalStorage.EMAIL_ADDRESS = response.email
                LocalStorage.AVATAR = avatarUrl
                LocalStorage.HIDDEN_HASH_FILES = true
                infoToast("Logged in")
                window.location.reload()
            } else warningToast("Username or password is incorrect!")
        }
    }

    showDialog(showLogoutDialog, "Logout", "Do you want to logout?", {}) {
        coroutineScope.launch {
            httpRequest("/@/api/logout").auth().acceptJson().jsonContentType().post()
            LocalStorage.TOKEN = null
            LocalStorage.CURRENT_USERNAME = null
            LocalStorage.AVATAR = null
            LocalStorage.EMAIL_ADDRESS = null
            window.location.href = "/#/"
            window.location.reload()
        }
    }
}
