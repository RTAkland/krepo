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
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.currentPath
import cn.rtast.kmvnrepo.entity.Contents
import cn.rtast.kmvnrepo.entity.DeleteGavRequest
import cn.rtast.kmvnrepo.util.*
import cn.rtast.kmvnrepo.util.file.LocalStorage
import cn.rtast.kmvnrepo.util.file.formatSize
import cn.rtast.kmvnrepo.util.string.fromJson
import cn.rtast.kmvnrepo.util.string.getDate
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.src
import dev.fritz2.core.storeOf
import dev.fritz2.remote.http
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.publicContentListingPage() {
    val showDeleteFileEntryDialog = storeOf(false)
    val selectedFileEntry = storeOf("")

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
            .auth().acceptJson().jsonContentType()
        val response = api.get()
        if (response.status == 404) {
            div("notification has-text-centered") { +"404 - Not Found" }
            return@launch
        }
        require(response.ok) { errorToast("获取文件列表失败!") }
        val responseJson = response.body().fromJson<Contents>()
        div("container") {
            br {}
            div("level-left") { h4("title is-4") { +"Index of $currentPath" } }
            hr {}
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
                            inlineStyle("cursor: pointer;")
                            clicks handledBy {
                                if (entry.isDirectory) {
                                    window.location.href = "/#$currentPath/${entry.name}"
                                } else {
                                    window.location.href = "$backend$currentPath/${entry.name}"
                                }
                            }
                            div("level") {
                                div("level-left") {
                                    if (entry.isDirectory) {
                                        className("has-text-dark")
                                        i { +"${entry.name}/" }
                                    } else {
                                        className("has-text-dark")
                                        b { +entry.name }
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
    showDialog(showDeleteFileEntryDialog, "删除构件/包", "是否删除以下内容?", {
        i { +selectedFileEntry.current }
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
}

