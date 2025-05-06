/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 21:06
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages.users

import cn.rtast.kmvnrepo.components.infoToast
import cn.rtast.kmvnrepo.components.navbar
import cn.rtast.kmvnrepo.components.pageFooter
import cn.rtast.kmvnrepo.components.showDialog
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.entity.GetUsersResponse
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.checkSession
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.string.fromJson
import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.userManagePage() {
    checkSession {
        navbar()
        val showDeleteUserDialog = storeOf(false)
        val selectedUser = storeOf<GetUsersResponse.User?>(null)
        coroutineScope.launch {
            val users = httpRequest("/@/api/user/")
                .auth().acceptJson().jsonContentType()
                .get().body().fromJson<GetUsersResponse>().data
            div("container") {
                h2("title is-3 mt-4 mb-4") { +"用户列表" }
                div("columns is-multiline") {
                    users.forEach { user ->
                        div("column is-one-third") {
                            div("card") {
                                div("card-content") {
                                    p("title is-5") { +user.name }
                                    p("subtitle is-6 has-text-grey") { +user.email }
                                    div("buttons") {
                                        button("button is-small is-link") {
                                            +"编辑"
                                            clicks handledBy {
                                                window.location.href = "/#/user/edit?username=${user.name}"
                                            }
                                        }
                                        button("button is-small is-danger") {
                                            +"删除"
                                            clicks handledBy {
                                                selectedUser.update(user)
                                                showDeleteUserDialog.update(true)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            pageFooter()
        }
        showDialog(showDeleteUserDialog, "删除用户", "是否要删除该用户", {}) {
            coroutineScope.launch {
                httpRequest("/@/api/user/${selectedUser.current!!.name}")
                    .auth().acceptJson().jsonContentType()
                    .delete()
                infoToast("删除成功")
                window.location.reload()
            }
            infoToast("正在删除中...")
        }
    }
}