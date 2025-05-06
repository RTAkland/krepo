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
import dev.fritz2.headless.components.tooltip
import dev.fritz2.remote.http
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.navbar() {
    val showLogoutDialog = storeOf(false)
    val showLoginDialog = storeOf(false)
    val username = storeOf("")
    val password = storeOf("")
    nav("navbar level") {
        inlineStyle("background-color: #5181B8")
        inlineStyle("color: #FFFFFF")
        attr("role", "navigation")
        attr("aria-label", "main navigation")
        div("navbar-brand") {
            a("navbar-item is-rounded") {
                href("#")
                +frontendConfig.pageTitle
            }.tooltip {
                +"Back to home page"
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
            if (LocalStorage.TOKEN != null) {
                id("navbarBasic")
                div("navbar-start") {
                    div("navbar-item has-dropdown is-hoverable") {
                        a("navbar-link") {
                            +"用户"
                        }
                        div("navbar-dropdown") {
                            a("navbar-item") {
                                href("/#/user/manage")
                                +"用户列表"
                            }
                            a("navbar-item") {
                                href("/#/user/create")
                                +"新增用户"
                            }
                        }
                    }
                    div("navbar-item has-dropdown is-hoverable") {
                        a("navbar-link") {
                            +"设置"
                        }
                        div("navbar-dropdown") {
                            a("navbar-item") {
                                +"前端设置"
                                href("/#/setting")
                            }
                            a("navbar-item") {
                                +"仓库设置"
                                href("/#/setting/repository")
                            }
                        }
                    }
                }
            }
            div("navbar-end") {
                div("navbar-item") {
                    div("buttons") {
                        if (LocalStorage.TOKEN == null) {
                            a("button is-light") {
                                +"登录"
                                clicks handledBy { showLoginDialog.update(true) }
                            }
                        } else {
                            figure("image is-32x26 mr-2") {
                                img {
                                    src(LocalStorage.AVATAR!!)
                                    attr("alt", "Avatar")
                                    className("is-rounded")
                                }
                            }
                            span { b { +LocalStorage.CURRENT_USERNAME!! } }
                            a("button is-light") {
                                +"登出"
                                clicks handledBy { showLogoutDialog.update(true) }
                            }
                        }
                    }
                }
            }
        }
    }
    showDialog(showLoginDialog, "登录", null, {
        div("field") {
            label("label") { +"用户名" }
            div("control") {
                input("input") {
                    type("text")
                    placeholder("请输入用户名")
                    value(username.data)
                    changes.values() handledBy username.update
                }
            }
        }
        div("field") {
            label("label") { +"密码" }
            div("control") {
                input("input") {
                    type("password")
                    placeholder("请输入密码")
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
                infoToast("登陆成功")
                window.location.reload()
            } else warningToast("用户名或密码不正确!")
        }
    }

    showDialog(showLogoutDialog, "退出登录", "是否要退出登录", {}) {
        coroutineScope.launch {
            httpRequest("/@/api/logout").auth().acceptJson().jsonContentType().post()
            LocalStorage.TOKEN = null
            LocalStorage.CURRENT_USERNAME = null
            LocalStorage.AVATAR = null
            LocalStorage.EMAIL_ADDRESS = null
            infoToast("登出成功")
            window.location.href = "/#/"
        }
        infoToast("正在退出登录...")
    }
}
