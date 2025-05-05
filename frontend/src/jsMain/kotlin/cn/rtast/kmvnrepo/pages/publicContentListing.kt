/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 15:28
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages

import cn.rtast.kmvnrepo.backend
import cn.rtast.kmvnrepo.components.errorToast
import cn.rtast.kmvnrepo.components.infoToast
import cn.rtast.kmvnrepo.components.showDialog
import cn.rtast.kmvnrepo.components.warningToast
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.currentPath
import cn.rtast.kmvnrepo.entity.Contents
import cn.rtast.kmvnrepo.entity.DeleteGavRequest
import cn.rtast.kmvnrepo.entity.LoginSuccessResponse
import cn.rtast.kmvnrepo.util.*
import dev.fritz2.core.*
import dev.fritz2.remote.http
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.publicContentListingPage() {
    val showLoginDialog = storeOf(false)
    val username = storeOf("")
    val password = storeOf("")
    val showDeleteFileEntryDialog = storeOf(false)
    val selectedFileEntry = storeOf("")
    val showLogoutDialog = storeOf(false)
    coroutineScope.launch {
        try {
            if (LocalStorage.TOKEN != null) {
                if (!http("$backend/@/api/user").auth().get().ok) {
                    LocalStorage.clearAll()
                    infoToast("登录已过期~")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val api = httpRequest("/@/api/contents/$currentPath")
        val response = api.get()
        if (response.status == 404) {
            div("notification has-text-centered") { +"404 - Not Found" }
            return@launch
        }
        require(response.ok) { errorToast("获取文件列表失败!") }
        val responseJson = response.body().fromJson<Contents>()
        div("container") {
            br {}
            nav("level") {
                div("level-left") {
                    h4("title is-4") { +"Index of $currentPath" }
                }
                div("level-right") {
                    if (LocalStorage.TOKEN == null) {
                        a("button is-light") {
                            +"登录 / Login"
                            clicks handledBy { showLoginDialog.update(true) }
                        }
                    } else {
                        figure("image is-32x32 mr-2") {
                            img {
                                src(LocalStorage.AVATAR!!)
                                attr("alt", "Avatar")
                                className("is-rounded")
                            }
                        }
                        a("button is-light") {
                            +"登出 / Logout"
                            clicks handledBy { showLogoutDialog.update(true) }
                        }
                    }
                }
            }
            hr { }
            if (currentPath.isNotEmpty()) {
                div("mb-4") {
                    a {
                        val parentPath = currentPath.trimEnd('/').substringBeforeLast('/', "")
                        href("/#$parentPath")
                        className("button is-link")
                        +"上一级 / Go up"
                    }
                }
            }
            div("columns is-multiline") {
                responseJson.data.sortedWith(compareBy({ !it.isDirectory }, { it.name })).forEach { entry ->
                    div("column is-one-third") {
                        div("box") {
                            div("level") {
                                div("level-left") {
                                    a {
                                        if (entry.isDirectory) {
                                            href("/#$currentPath/${entry.name}")
                                            className("has-text-dark")
                                            i { +"${entry.name}/" }
                                        } else {
                                            href("$backend$currentPath/${entry.name}")
                                            className("has-text-dark")
                                            b { +entry.name }
                                        }
                                    }
                                }
                                if (LocalStorage.TOKEN != null) {
                                    div("level-right") {
                                        button("button is-danger is-small") {
                                            img {
                                                src("assets/trash.svg")
                                                attr("alt", "Delete icon")
                                                inlineStyle("width: 1.2rem; height: 1.2rem;")
                                            }
                                            clicks handledBy {
                                                showDeleteFileEntryDialog.update(true)
                                                selectedFileEntry.update("$currentPath/${entry.name}".removePrefix("/"))
                                            }
                                        }
                                    }
                                }
                            }
                            div("level is-mobile") {
                                div("level-left") {
                                    i { span("has-text-grey") { +getDate(entry.timestamp) } }
                                }
                                div("level-right") {
                                    span("has-text-grey") {
                                        b { if (!entry.isDirectory) +formatSize(entry.size) else +"-" }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            hr { className("mt-6") }
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
                infoToast("登陆成功")
                window.location.reload()
            } else warningToast("用户名或密码不正确!")
        }
    }

    showDialog(showDeleteFileEntryDialog, "删除构件/包", "是否删除一下内容?", {
        i {
            +selectedFileEntry.current
        }
    }) {
        coroutineScope.launch {
            httpRequest("/@/api/gav")
                .acceptJson().jsonContentType().auth()
                .setBody(DeleteGavRequest(selectedFileEntry.current))
                .delete()
            window.location.reload()
        }
        infoToast("正在删除中...")
    }

    showDialog(showLogoutDialog, "退出登录", "是否要退出登录", {}) {
        coroutineScope.launch {
            httpRequest("/@/api/logout").auth().acceptJson().jsonContentType().post()
            LocalStorage.TOKEN = null
            LocalStorage.CURRENT_USERNAME = null
            LocalStorage.AVATAR = null
            LocalStorage.EMAIL_ADDRESS = null
            infoToast("登出成功")
            window.location.reload()
        }
        infoToast("正在退出登录...")
    }
}
