/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:28 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.pages.settings

import dev.fritz2.core.*
import krepo.backendVersion
import krepo.components.fa.autoSvg
import krepo.components.showDialog
import krepo.coroutineScope
import krepo.entity.maven.GetRepositoriesResponse
import krepo.entity.maven.RepositoryVisibility
import krepo.util.auth
import krepo.util.byte.fromProtoBuf
import krepo.util.file.checkPermission
import krepo.util.httpRequest
import krepo.util.launchJob
import krepo.util.notImplToast

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

fun RenderContext.MavenRepositorySettingPage() {
    checkPermission {
        val showCreateRepositoryFormDialog = storeOf(false)
        val showDeleteRepositoryDialog = storeOf(false)
        val nameStore = storeOf("")
        val visibilityStore = storeOf(RepositoryVisibility.Public)
        val allowedExtensionsStore = storeOf("jar\naar\nklib")
        val snapshotStore = storeOf(false)
        val selectedRepositoryName = storeOf("")
        val showModifyRepositoryStore = storeOf(false)
        coroutineScope.launchJob {
            val repositories = httpRequest(backendVersion.LIST_ALL_REPOSITORIES)
                .auth().get().arrayBuffer().fromProtoBuf<GetRepositoriesResponse>().data
            section("container") {
                inlineStyle("max-width: 60%")
                div("level") {
                    div("level-left") { h3("title is-4") { +"Repository List" } }
                    div("level-right") {
                        button("button") {
                            autoSvg("fa-plus")
                            +"Add Repository"
                            clicks handledBy { showCreateRepositoryFormDialog.update(true) }
                        }
                    }
                }
                repositories.forEach { repo ->
                    div("box is-flex is-justify-content-space-between is-align-items-center") {
                        span {
                            when (repo.visibility) {
                                RepositoryVisibility.Internal -> autoSvg("fa-eye-slash")
                                RepositoryVisibility.Public -> autoSvg("fa-eye")
                            }
                            a("is-size-5") {
                                +repo.name
                                href("/#/${repo.name}")
                                title("Click to view the content of this repository")
                            }
                            span("tag ml-2") { +repo.visibility.desc }
                        }
                        div {
                            button("button is-small mr-2") {
                                autoSvg("fa-user-pen")
                                +"Edit"
                                clicks handledBy {
                                    selectedRepositoryName.update(repo.name)
                                    showModifyRepositoryStore.update(true)
                                }
                            }
                            button("button is-small") {
                                autoSvg("fa-trash-alt")
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
            coroutineScope.launchJob {
//                val result = httpRequest(backendVersion.DELETE_REPOSITORY)
//                    .auth().setOctetBody(mapOf("name" to selectedRepositoryName.current))
//                    .delete().checkImpl(CheckImplType.Toast)
//                    .arrayBuffer().fromProtoBuf<DeleteRepositoryResponse>()
//                when (result.code) {
//                    200 -> infoToast(result.message)
//                    409 -> warningToast(result.message)
//                    else -> errorToast("Unknown error!")
//                }
//                window.location.reload()
                notImplToast()
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
            coroutineScope.launchJob {
//                val name = nameStore.current
//                val visibility = visibilityStore.current
//                val allowedExtensions = allowedExtensionsStore.current
//                val allowSnapshot = snapshotStore.current
//                val result = httpRequest(backendVersion.CREATE_REPOSITORY)
//                    .auth().setOctetBody(
//                        ConfigRepository(
//                            name, visibility, allowedExtensions.split("\n"), allowSnapshot,
//                            RepositoryStatus.Created,
//                            listOf()
//                        )
//                    ).post().arrayBuffer().fromProtoBuf<CreateRepositoryResponse>()
//                when (result.code) {
//                    200 -> infoToast(result.message)
//                    409 -> warningToast(result.message)
//                    else -> errorToast("Unknown error!")
//                }
//                window.location.reload()
                notImplToast()
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
            coroutineScope.launchJob {
//                val name = nameStore.current
//                val visibility = visibilityStore.current
//                val allowedExtensions = allowedExtensionsStore.current
//                val allowSnapshot = snapshotStore.current
//                val result = httpRequest(backendVersion.MODIFY_REPOSITORY)
//                    .auth().setOctetBody(
//                        ModifyRepository(
//                            selectedRepositoryName.current,
//                            name,
//                            visibility,
//                            allowedExtensions.split("\n"),
//                            allowSnapshot,
//                            RepositoryStatus.Modified
//                        )
//                    ).put().arrayBuffer().fromProtoBuf<ModifyRepositoryResponse>()
//                when (result.code) {
//                    200 -> infoToast(result.message)
//                    409 -> warningToast(result.message)
//                    else -> errorToast("Unknown error!")
//                }
//                window.location.reload()
                notImplToast()
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
    action: RenderContext.() -> Unit,
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
