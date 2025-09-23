/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 18:16
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.pages.settings

import cn.rtast.krepo.components.badge
import cn.rtast.krepo.components.errorToast
import cn.rtast.krepo.components.infoToast
import cn.rtast.krepo.components.showDialog
import cn.rtast.krepo.components.warningToast
import cn.rtast.krepo.coroutineScope
import cn.rtast.krepo.enums.BadgeType
import cn.rtast.krepo.enums.RepositoryStatus
import cn.rtast.krepo.util.auth
import cn.rtast.krepo.util.file.checkSession
import cn.rtast.krepo.util.httpRequest
import cn.rtast.krepo.util.jsonContentType
import cn.rtast.krepo.util.setBody
import cn.rtast.krepo.util.string.fromJson
import cn.rtast.krepo.util.string.toJson
import cn.rtast.krepo.entity.ModifyRepository
import cn.rtast.krepo.entity.RepositoryVisibility
import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import cn.rtast.krepo.entity.CreateRepository
import cn.rtast.krepo.entity.CreateRepositoryResponse
import cn.rtast.krepo.entity.DeleteRepositoryResponse
import cn.rtast.krepo.entity.GetRepositoriesResponse
import cn.rtast.krepo.entity.ModifyRepositoryResponse

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
            section("container") {
                inlineStyle("max-width: 60%")
                div("level") {
                    div("level-left") { h3("title is-4") { +"Repository List" } }
                    div("level-right") {
                        button("button is-primary") {
                            i("fa-solid fa-plus mr-2") {}
                            +"Add Repository"
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
                                title("Click to view the content of this repository")
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
                                +"Edit"
                                clicks handledBy {
                                    selectedRepositoryName.update(repo.name)
                                    showModifyRepositoryStore.update(true)
                                }
                            }
                            button("button is-small is-danger") {
                                i("fa-solid fa-trash mr-2") {}
                                +"Delete"
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
        showDialog(
            showDeleteRepositoryDialog,
            "Delete the repository",
            "Do you want to delete the repository? Deleting a repository is only a logical deletion, the folder will not be deleted",
            {}) {
            coroutineScope.launch {
                val result = httpRequest("/@/api/repositories/delete")
                    .auth().acceptJson().jsonContentType()
                    .setBody(mapOf("name" to selectedRepositoryName.current))
                    .delete().body().fromJson<DeleteRepositoryResponse>()
                when (result.code) {
                    200 -> infoToast(result.message)
                    409 -> warningToast(result.message)
                    else -> errorToast("Unknown error!")
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
            "Create Repository",
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
                            RepositoryStatus.Created,
                            listOf()
                        )
                    )
                    .post().body().fromJson<CreateRepositoryResponse>()
                when (result.code) {
                    200 -> infoToast(result.message)
                    409 -> warningToast(result.message)
                    else -> errorToast("Unknown error!")
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
            "Update repository settings",
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
                    else -> errorToast("Unknown error!")
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
                label("label") { +"Repository name" }
                div("control") {
                    input("input") {
                        placeholder("Fill in the repository name")
                        value(selectedRepo.data)
                        changes.values() handledBy nameStore.update
                    }
                }
            }
            div("field") {
                label("label") { +"Visibility" }
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
                label("label") { +"File extensions allowed to be uploaded (one per line)" }
                div("control") {
                    textarea("textarea") {
                        placeholder("Fill the allowed extensions")
                        value(allowedExtensionsStore.data)
                        changes.values() handledBy allowedExtensionsStore.update
                    }
                }
            }
            prettyCheckbox("Is allow snapshot version?", snapshotStore)
        }
    }, action)
}
