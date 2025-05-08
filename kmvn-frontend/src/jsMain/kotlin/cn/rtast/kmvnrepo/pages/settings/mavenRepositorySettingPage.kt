/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 18:16
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages.settings

import cn.rtast.kmvnrepo.components.*
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.entity.*
import cn.rtast.kmvnrepo.enums.BadgeType
import cn.rtast.kmvnrepo.enums.RepositoryStatus
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.checkSession
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.setBody
import cn.rtast.kmvnrepo.util.string.fromJson
import cn.rtast.kmvnrepo.util.string.toJson
import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.prettyCheckbox(labelText: String, store: Store<Boolean>) {
    div("field mt-3") {
        div("control") {
            label("checkbox is-flex is-align-items-center") {
                input("mr-2") {
                    type("checkbox")
                    checked(store.data)
                    changes.values() handledBy { store.update(it.toBoolean()) }
                }
                +labelText
            }
        }
    }
}

fun RenderContext.mavenRepositorySettingPage() {
    checkSession {
        val showCreateRepositoryFormDialog = storeOf(false)
        val showDeleteRepositoryDialog = storeOf(false)
        val nameStore = storeOf("")
        val visibilityStore = storeOf(RepositoryVisibility.Public)
        val allowedExtensionsStore = storeOf("jar\naar\nklib")
        val snapshotStore = storeOf(false)
        val selectedRepositoryName = storeOf("")
        val showModifyRepositoryStore = storeOf(false)
        coroutineScope.launch {
            val repositories = httpRequest("/@/api/repositories/all")
                .auth().acceptJson().jsonContentType()
                .get().body().fromJson<GetRepositoriesResponse>().data
            section("section") {
                div("container") {
                    div("level") {
                        div("level-left") {
                            h3("title is-4") {
                                i("fa-solid fa-house mr-2") {}
                                +"仓库列表"
                            }
                        }
                        div("level-right") {
                            button("button is-primary") {
                                i("fa-solid fa-plus mr-2") {}
                                +"添加仓库"
                                clicks handledBy { showCreateRepositoryFormDialog.update(true) }
                            }
                        }
                    }
                    repositories.forEach { repo ->
                        div("box is-flex is-justify-content-space-between is-align-items-center") {
                            span {
                                when (repo.visibility) {
                                    RepositoryVisibility.Internal -> i("fa-solid fa-eye-slash mr-2") {}
                                    RepositoryVisibility.Public -> i("fa-solid fa-eye mr-2") {}
                                }
                                a("is-size-5") {
                                    +repo.name
                                    href("/#/${repo.name}")
                                    title("点击查看仓库内容 / Click to view the content of this repository")
                                }
                                i {
                                    p {
                                        when (repo.visibility) {
                                            RepositoryVisibility.Internal -> inlineStyle("color: purple;")
                                            RepositoryVisibility.Public -> inlineStyle("color: green;")
                                        }
                                        when (repo.status) {
                                            RepositoryStatus.Deleted -> badge(BadgeType.Deleted) { i("fa-solid fa-xmark mr-2") {} }
                                            RepositoryStatus.Created -> badge(BadgeType.Created) { i("fa-solid fa-hourglass mr-2") {} }
                                            RepositoryStatus.Available -> badge(BadgeType.Available) { i("fa-solid fa-check mr-2") {} }
                                            RepositoryStatus.Modified -> badge(BadgeType.Modified) { i("fa-solid fa-hourglass mr-2") {} }
                                        }
                                        +repo.visibility.desc
                                    }
                                }
                            }
                            div {
                                button("button is-small is-info mr-2") {
                                    i("fa-solid fa-user-pen mr-2") {}
                                    +"编辑"
                                    clicks handledBy {
                                        selectedRepositoryName.update(repo.name)
                                        showModifyRepositoryStore.update(true)
                                    }
                                }
                                button("button is-small is-danger") {
                                    i("fa-solid fa-trash mr-2") {}
                                    +"删除"
                                    clicks handledBy {
                                        selectedRepositoryName.update(repo.name)
                                        showDeleteRepositoryDialog.update(true)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        showDialog(
            showDeleteRepositoryDialog,
            "删除仓库",
            "是否要删除仓库? 删除仓库只是逻辑上删除, 文件夹并不会被删除",
            {}) {
            coroutineScope.launch {
                val result = httpRequest("/@/api/repositories/delete")
                    .auth().acceptJson().jsonContentType()
                    .setBody(mapOf("name" to selectedRepositoryName.current))
                    .delete().body().fromJson<DeleteRepositoryResponse>()
                when (result.code) {
                    200 -> infoToast(result.message)
                    409 -> warningToast(result.message)
                    else -> errorToast("未知错误!")
                }
                window.location.reload()
            }
        }
        createModifyRepositoryDialog(
            showCreateRepositoryFormDialog,
            nameStore,
            visibilityStore,
            allowedExtensionsStore,
            snapshotStore,
            "创建仓库",
            selectedRepositoryName
        ) {
            coroutineScope.launch {
                val name = nameStore.current
                val visibility = visibilityStore.current
                val allowedExtensions = allowedExtensionsStore.current
                val allowSnapshot = snapshotStore.current
                val result = httpRequest("/@/api/repositories/new")
                    .auth().acceptJson().jsonContentType()
                    .setBody(
                        CreateRepository(
                            name, visibility, allowedExtensions.split("\n"), allowSnapshot,
                            RepositoryStatus.Created
                        )
                    )
                    .post().body().fromJson<CreateRepositoryResponse>()
                when (result.code) {
                    200 -> infoToast(result.message)
                    409 -> warningToast(result.message)
                    else -> errorToast("未知错误!")
                }
                window.location.reload()
            }
        }
        createModifyRepositoryDialog(
            showModifyRepositoryStore,
            nameStore,
            visibilityStore,
            allowedExtensionsStore,
            snapshotStore,
            "修改仓库配置",
            selectedRepositoryName
        ) {
            coroutineScope.launch {
                val name = nameStore.current
                val visibility = visibilityStore.current
                val allowedExtensions = allowedExtensionsStore.current
                val allowSnapshot = snapshotStore.current
                val result = httpRequest("/@/api/repositories/modify")
                    .auth().acceptJson().jsonContentType()
                    .setBody(
                        ModifyRepository(
                            selectedRepositoryName.current,
                            name,
                            visibility,
                            allowedExtensions.split("\n"),
                            allowSnapshot,
                            RepositoryStatus.Modified
                        ).apply { println(this.toJson()) }
                    ).put().body().fromJson<ModifyRepositoryResponse>()
                when (result.code) {
                    200 -> infoToast(result.message)
                    409 -> warningToast(result.message)
                    else -> errorToast("未知错误!")
                }
                window.location.reload()
            }
        }
    }
}

fun RenderContext.createModifyRepositoryDialog(
    showDialog: Store<Boolean>,
    nameStore: Store<String>,
    visibilityStore: Store<RepositoryVisibility>,
    allowedExtensionsStore: Store<String>,
    snapshotStore: Store<Boolean>,
    title: String,
    selectedRepo: Store<String>,
    action: RenderContext.() -> Unit
) {
    showDialog(showDialog, title, null, {
        div("box") {
            div("field") {
                label("label") { +"仓库名称" }
                div("control") {
                    input("input") {
                        placeholder("输入仓库名称")
                        value(selectedRepo.data)
                        changes.values() handledBy nameStore.update
                    }
                }
            }
            div("field") {
                label("label") { +"可见性" }
                RepositoryVisibility.entries.forEach { visibility ->
                    div("control") {
                        label("radio is-flex is-align-items-center mb-2") {
                            input("mr-2") {
                                type("radio")
                                name("visibility")
                                if (visibility == RepositoryVisibility.Public) checked(true)
                                changes.values() handledBy { visibilityStore.update(visibility) }
                            }
                            +visibility.desc
                        }
                    }
                }
            }
            div("field") {
                label("label") { +"允许上传的文件后缀(每行一个)" }
                div("control") {
                    textarea("textarea") {
                        placeholder("填写允许的后缀名")
                        value(allowedExtensionsStore.data)
                        changes.values() handledBy allowedExtensionsStore.update
                    }
                }
            }
            prettyCheckbox("允许发布快照版本", snapshotStore)
        }
    }, action)
}
