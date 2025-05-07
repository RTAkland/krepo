/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 15:28
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages

import cn.rtast.kmvnrepo.backend
import cn.rtast.kmvnrepo.components.*
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.currentPath
import cn.rtast.kmvnrepo.entity.Contents
import cn.rtast.kmvnrepo.entity.DeleteGavRequest
import cn.rtast.kmvnrepo.getDependenciesTemplate
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.LocalStorage
import cn.rtast.kmvnrepo.util.file.formatSize
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.setBody
import cn.rtast.kmvnrepo.util.string.fromJson
import cn.rtast.kmvnrepo.util.string.getDate
import cn.rtast.kmvnrepo.util.string.parseGAV
import dev.fritz2.core.*
import dev.fritz2.headless.components.tooltip
import dev.fritz2.remote.http
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

fun RenderContext.publicContentListingPage() {
    val showDeleteFileEntryDialog = storeOf(false)
    val selectedFileEntry = storeOf("")
    val showLocalConfigDialog = storeOf(false)
    val hiddenHashFilesToggle = storeOf(true)
    navbar()
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
        val artifacts = responseJson.data.toMutableList()
        div("container") {
            br {}
            div("level-left") { h4("title is-4") { +"Index of $currentPath" } }
            hr {}
            if (currentPath.isNotEmpty()) {
                div("mb-4") {
                    div("level") {
                        div("level-left") {
                            a {
                                val parentPath = currentPath.trimEnd('/').substringBeforeLast('/', "")
                                href("/#$parentPath")
                                className("button is-link")
                                +"上一级"
                            }.tooltip { +"GO UP" }
                        }
                        div("level-right") {
                            if (artifacts.any {
                                    it.name != "maven-metadata.xml" &&
                                            it.name != "maven-metadata.xml.sha1" &&
                                            it.name != "maven-metadata.xml.sha256" &&
                                            it.name != "maven-metadata.xml.sha512" &&
                                            it.name.endsWith("sha1")
                                }) {
                                a {
                                    img {
                                        src("assets/link.svg")
                                        inlineStyle("width: 1.5rem; height: 1.5rem; margin-right: 0.5rem;")
                                    }
                                    className("button is-link")
                                    +" 复制构件坐标"
                                    clicks handledBy {
                                        val simplePath = currentPath
                                            .removeSuffix("/").split("/").drop(2)
                                            .joinToString("/")
                                        val (group, name, version) = parseGAV(simplePath)
                                        window.navigator.clipboard.writeText(
                                            getDependenciesTemplate(group, name, version)
                                        )
                                        infoToast("复制成功!")
                                    }
                                }.tooltip { +"Copy the artifact coordinate to clipboard" }
                            }
                            a {
                                img {
                                    src("assets/setting.svg")
                                    inlineStyle("width: 1.5rem; height: 1.5rem; margin-right: 0.5rem;")
                                }
                                className("button is-link mr-2")
                                +" 设置"
                                clicks handledBy { showLocalConfigDialog.update(true) }
                            }.tooltip { +"Settings" }
                        }
                    }
                }
            }
            div("columns is-multiline") {
                if (LocalStorage.HIDDEN_HASH_FILES) artifacts.removeAll {
                    !it.isDirectory &&
                            (it.name.endsWith("md5")
                                    || it.name.endsWith("sha256")
                                    || it.name.endsWith("sha512")
                                    || it.name.endsWith("sha1")
                                    || it.name.endsWith("asc"))
                }
                artifacts.sortedWith(compareBy({ !it.isDirectory }, { it.name })).forEach { entry ->
                    div("column is-one-third") {
                        div("box") {
                            inlineStyle("cursor: pointer;")
                            div("level") {
                                div("level-left") {
                                    div("truncate-text") {
                                        className("has-text-dark")
                                        if (entry.isDirectory) {
                                            i {
                                                +"${entry.name}/"
                                                clicks handledBy {
                                                    window.location.href = "/#$currentPath/${entry.name}"
                                                }
                                            }
                                        } else {
                                            b {
                                                +entry.name
                                                clicks handledBy {
                                                    window.location.href = "$backend$currentPath/${entry.name}"
                                                }
                                            }
                                        }
                                    }
                                }
                                div("level-right") {
                                    if (LocalStorage.TOKEN != null) {
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
        pageFooter()
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
    showDialog(showLocalConfigDialog, "更改配置", null, {
        div("mb-4") {
            label("switch") {
                input {
                    type("checkbox")
                    id("hiddenHashFiles")
                    inlineStyle("display: none;")
                    className("switch-checkbox")
                    checked(LocalStorage.HIDDEN_HASH_FILES)
                    changes.values() handledBy { hiddenHashFilesToggle.update(it.toBoolean()) }
                }
                span("slider round") {}
            }
            span("ml-2") { +"隐藏 .sha1 / .sha256 / .sha512 / .md5 / .asc 等文件列出" }
                .tooltip { +"Hidden hash files like .sha1 / .sha256 / .sha512 / .md5 / .asc to prevent them be listed" }
        }
    }) {
        LocalStorage.HIDDEN_HASH_FILES = hiddenHashFilesToggle.current.toString().toBoolean()
        infoToast("保存成功")
        window.setTimeout({ window.location.reload() }, 0.5.seconds.inWholeMilliseconds.toInt())
    }
}

