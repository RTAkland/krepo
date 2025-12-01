/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 7:19 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:Suppress("FunctionName")

package krepo.pages.settings

import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import dev.fritz2.core.title
import kotlinx.browser.window
import krepo.backendVersion
import krepo.components.errorToast
import krepo.components.infoToast
import krepo.components.showDialog
import krepo.coroutineScope
import krepo.entity.maven.ConfigRepositoryWithSize
import krepo.index.IndexRepositoryResponse
import krepo.index.req.DeleteIndexRequest
import krepo.index.req.RebuildIndexRequest
import krepo.util.auth
import krepo.util.file.checkPermission
import krepo.util.file.formatSize
import krepo.util.fromJson
import krepo.util.httpRequest
import krepo.util.launchJob
import krepo.util.repo.getRepositories
import krepo.util.setBody

fun RenderContext.IndexSettingsPage() = checkPermission {
    val repositories = storeOf<List<ConfigRepositoryWithSize>>(emptyList())
    val showRebuildIndexDialog = storeOf(false)
    val showDeleteIndexDialog = storeOf(false)
    val selectedRepositoryStore = storeOf<String?>(null)

    div("container mt-5 has-text-centered") {
        h2("title is-2 mb-5") { +"Index management" }
        div("box mx-auto") {
            inlineStyle("max-width: 50%")
            repositories.data.render { repoList ->
                if (repoList.isEmpty()) {
                    p { +"Loading repositories..." }
                } else {
                    table("table is-fullwidth is-hoverable is-striped") {
                        thead {
                            tr {
                                th("has-text-centered") { +"Repository" }
                                th("has-text-centered") { +"Status" }
                                th("has-text-centered") { +"Size" }
                                th("has-text-centered") { +"Actions" }
                            }
                        }
                        tbody {
                            repoList.forEach { repo ->
                                tr {
                                    td("has-text-centered") { +repo.name }
                                    td("has-text-centered") { span("tag") { +repo.status.toString() } }
                                    td("has-text-centered") { span("tag") { +formatSize(repo.size ?: 0L) } }
                                    td("has-text-centered") {
                                        button("button is-warning is-light is-small mr-2") {
                                            +"Rebuild"
                                            clicks handledBy {
                                                showRebuildIndexDialog.update(true)
                                                selectedRepositoryStore.update(repo.name)
                                            }
                                            title("Rebuild indexes or create indexed")
                                        }
                                        button("button is-danger is-light is-small") {
                                            +"Delete"
                                            clicks handledBy {
                                                showDeleteIndexDialog.update(true)
                                                selectedRepositoryStore.update(repo.name)
                                            }
                                            title("Delete indexes")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    showDialog(
        showRebuildIndexDialog,
        "Rebuild/Create indexes",
        "Rebuild all repositories indexes? (It's a dangerous action!)",
        {}) {
        coroutineScope.launchJob {
            infoToast("Rebuilding/Creating indexes...")
            if (selectedRepositoryStore.current != null) {
                val resp = httpRequest(backendVersion.CREATE_REPOSITORY_INDEX)
                    .auth().setBody(RebuildIndexRequest(selectedRepositoryStore.current!!))
                    .post().body().fromJson<IndexRepositoryResponse>()
                if (resp.success) {
                    infoToast("Rebuild/Create indexes success, taking ${resp.consuming} seconds")
                } else errorToast("Rebuild/Create indexes failed ${resp.message}")
            } else errorToast("No repository selected.")
        }
    }

    showDialog(
        showDeleteIndexDialog,
        "Delete indexes",
        "Delete all indexes? (It's a dangerous action!)", {}) {
        coroutineScope.launchJob {
            infoToast("Deleting indexes...")
            if (selectedRepositoryStore.current != null) {
                httpRequest(backendVersion.DELETE_REPOSITORY_INDEX)
                    .auth().setBody(DeleteIndexRequest(selectedRepositoryStore.current!!))
                    .delete().body()
                infoToast("Indexes deleted.")
                window.location.reload()
            } else errorToast("No repository selected.")
        }
    }

    coroutineScope.launchJob {
        repositories.update(getRepositories())
    }
}

