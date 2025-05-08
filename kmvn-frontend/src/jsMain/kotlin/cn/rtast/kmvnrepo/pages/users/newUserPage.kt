/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 21:07
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages.users

import cn.rtast.kmvnrepo.components.*
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.checkSession
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.setBody
import cn.rtast.kmvnrepo.util.string.fromJson
import cn.rtast.kmvnrepo.util.string.validateEmail
import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.newUserPage() {
    checkSession {
        navbar()
        val username = storeOf("")
        val email = storeOf("")
        val password = storeOf("")
        val showCreateUserDialog = storeOf(false)

        div("section") {
            div("container") {
                inlineStyle("max-width: 600px;")
                h3("title is-3 has-text-centered mb-6") { +"创建用户" }
                div("box mx-auto") {
                    div("columns is-multiline") {
                        div("column is-full") {
                            div("field") {
                                label("label") { +"用户名" }
                                div("control has-icons-left") {
                                    input("input") {
                                        type("text")
                                        placeholder("请输入用户名")
                                        value(username.data)
                                        changes.values() handledBy username.update
                                    }
                                    span("icon is-small is-left") {
                                        i("fas fa-user") {}
                                    }
                                }
                            }
                        }
                        div("column is-full") {
                            div("field") {
                                label("label") { +"邮件地址" }
                                div("control has-icons-left") {
                                    input("input") {
                                        type("email")
                                        placeholder("请输入邮件地址")
                                        value(email.data)
                                        changes.values() handledBy email.update
                                    }
                                    span("icon is-small is-left") {
                                        i("fas fa-envelope") {}
                                    }
                                }
                            }
                        }
                        div("column is-full") {
                            div("field") {
                                label("label") { +"密码" }
                                div("control has-icons-left") {
                                    input("input") {
                                        type("password")
                                        placeholder("请输入密码")
                                        value(password.data)
                                        changes.values() handledBy password.update
                                    }
                                    span("icon is-small is-left") {
                                        i("fas fa-lock") {}
                                    }
                                }
                            }
                        }
                        div("column is-full") {
                            div("field is-grouped is-grouped-right mt-5") {
                                div("control") {
                                    button("button is-link") {
                                        i("fa-solid fa-plus mr-2") {}
                                        +"创建"
                                        clicks handledBy { showCreateUserDialog.update(true) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        showDialog(showCreateUserDialog, "创建用户", "是否确定创建用户", {}) {
            if (username.current.isBlank() || password.current.isBlank() || email.current.isBlank()) {
                warningToast("请填写所有必要的的信息")
            } else {
                if (validateEmail(email)) {
                    val requestBody = mapOf(
                        "name" to username.current,
                        "password" to password.current,
                        "email" to email.current
                    )
                    coroutineScope.launch {
                        val result = httpRequest("/@/api/user")
                            .auth().acceptJson().jsonContentType()
                            .setBody(requestBody).post().body().fromJson<Map<String, String>>()
                        val code = result["code"]!!.toInt()
                        if (code == 200) {
                            infoToast("用户创建成功")
                            window.location.href = "/#/user/manage"
                        } else {
                            errorToast("创建用户失败! ${result["message"]}")
                        }
                    }
                    infoToast("正在创建中...")
                }
            }
        }
        pageFooter()
    }
}
