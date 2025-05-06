/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 21:44
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages.users

import cn.rtast.kmvnrepo.components.*
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.currentPath
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.checkSession
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.setBody
import cn.rtast.kmvnrepo.util.string.validateEmail
import dev.fritz2.core.RenderContext
import dev.fritz2.core.disabled
import dev.fritz2.core.placeholder
import dev.fritz2.core.storeOf
import dev.fritz2.core.type
import dev.fritz2.core.value
import dev.fritz2.core.values
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.editUserPage() {
    checkSession {
        navbar()
        val user = currentPath.split("?").last().split("&").first().split("=").last()
        val email = storeOf("")
        val password = storeOf("")
        val showUpdateUserDialog = storeOf(false)
        div("section") {
            div("container") {
                h3("title is-3 has-text-centered mb-6") { +"创建用户" }
                div("box") {
                    div("field") {
                        label("label") { +"用户名" }
                        div("control has-icons-left") {
                            input("input") {
                                disabled(true)
                                type("text")
                                placeholder("用户名")
                                value(user)
                            }
                            span("icon is-small is-left") {
                                i("fas fa-user") {}
                            }
                        }
                    }
                    div("field") {
                        label("label") { +"邮件地址" }
                        div("control has-icons-left") {
                            input("input") {
                                type("email")
                                placeholder("输入邮件地址")
                                value(email.data)
                                changes.values() handledBy email.update
                            }
                            span("icon is-small is-left") {
                                i("fas fa-envelope") {}
                            }
                        }
                    }
                    div("field") {
                        label("label") { +"密码" }
                        div("control has-icons-left") {
                            input("input") {
                                type("password")
                                placeholder("输入密码")
                                value(password.data)
                                changes.values() handledBy password.update
                            }
                            span("icon is-small is-left") {
                                i("fas fa-lock") {}
                            }
                        }
                    }
                    div("field is-grouped is-grouped-right mt-5") {
                        div("control") {
                            button("button is-link") {
                                +"更新"
                                clicks handledBy { showUpdateUserDialog.update(true) }
                            }
                        }
                    }
                }
            }
        }
        showDialog(showUpdateUserDialog, "更新用户信息", "是否确定更新用户信息", {}) {
            if (password.current.isBlank() || email.current.isBlank()) {
                warningToast("请填写所有必要的的信息")
            } else {
                if (validateEmail(email)) {
                    val requestBody = mapOf(
                        "name" to user,
                        "password" to password.current,
                        "email" to email.current
                    )
                    coroutineScope.launch {
                        httpRequest("/@/api/user/$user")
                            .auth().acceptJson().jsonContentType()
                            .setBody(requestBody).put().body()
                        infoToast("用户信息更新成功")
                        window.location.href = "/#/user/manage"
                    }
                }
            }
        }
        pageFooter()
    }
}