/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:30 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.pages.search

import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import krepo.backendVersion
import krepo.components.errorToast
import krepo.components.fa.svg
import krepo.components.fa.svgBlock
import krepo.components.infoToast
import krepo.components.warningToast
import krepo.coroutineScope
import krepo.entity.maven.ConfigRepositoryWithSize
import krepo.index.IndexMetadata
import krepo.index.IndexSearchResponse
import krepo.util.*
import krepo.util.byte.toByteArray
import krepo.util.repo.getRepositories
import krepo.util.string.extractQueryParams
import krepo.util.string.formatDate

fun RenderContext.SearchPage() {
    val queryParam = extractQueryParams(window.location.href)
    val keywordStore = storeOf(queryParam["k"] ?: "")
    val repoListStore = storeOf<List<ConfigRepositoryWithSize>>(emptyList())
    val selectedRepoStore = storeOf("releases")
    val resultStore = storeOf<IndexSearchResponse?>(null)
    val isButtonDisabled = storeOf(false)
    val isLoading = storeOf(false)
    val isLoadingClassName = storeOf("")
    val searchButtonTextStore = storeOf("Search")

    fun searchInner() {
        if (!isButtonDisabled.current) {
            isButtonDisabled.update(true)
            isLoading.update(true)
            isLoadingClassName.update("is-loading")
            searchButtonTextStore.update("Waiting 5 secs")
            coroutineScope.launchJob {
                if (keywordStore.current.length < 3) {
                    errorToast("Keyword must be more than 3 characters.")
                    isLoadingClassName.update("")
                } else {
                    search(keywordStore.current, selectedRepoStore.current, resultStore, isLoading)
                    isLoadingClassName.update("")
                    delay(5000) // Wait for 5 seconds
                }
                searchButtonTextStore.update("Search")
                isButtonDisabled.update(false)
                isLoading.update(false)
            }
        } else warningToast("Please wait 5 seconds before trying again")
    }

    div("container mt-5") {
        h2("title is-3 has-text-centered mb-5") { +"Search Artifacts" }
        div("box p-5 mx-auto") {
            inlineStyle("max-width: 900px;")
            div("columns is-vcentered") {
                div("column is-7 pr-1") {
                    input("input is-medium") {
                        placeholder("Artifact id")
                        value(keywordStore.data)
                        changes.values() handledBy keywordStore.update
                        title("Typing artifact id to search")
                    }
                }
                div("column is-2") {
                    inlineStyle("margin-right: 0.2rem;")
                    div("dropdown is-hoverable") {
                        div("dropdown-trigger") {
                            button("button is-medium") {
                                inlineStyle("width: 135px;")
                                attr("aria-haspopup", "true")
                                attr("aria-controls", "dropdown-menu")
                                span {
                                    selectedRepoStore.data.render { selected ->
                                        +selected.ifEmpty { "releases" }
                                    }
                                }
                                span("icon is-small") {
                                    svgBlock("fa-angle-down", "", size = 14) {
                                        attr("aria-hidden", "true")
                                    }
                                }
                                title("Select repository to search")
                            }
                        }
                        div("dropdown-menu") {
                            id("dropdown-menu")
                            attr("role", "menu")
                            div("dropdown-content") {
                                inlineStyle("width: 135px;")
                                repoListStore.data.combine(selectedRepoStore.data) { list, selected ->
                                    list to selected
                                }.render { (list, selected) ->
                                    if (list.isEmpty()) {
                                        a("dropdown-item") { +"Loading repos..." }
                                    } else {
                                        list.forEach { repo ->
                                            val isActive = repo.name == selected
                                            a("dropdown-item${if (isActive) " is-active" else ""}") {
                                                +repo.name
                                                clicks handledBy {
                                                    selectedRepoStore.update(repo.name)
                                                    updateURI(keywordStore.current, repo.name)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                div("column is-3") {
                    button("button is-fullwidth is-medium") {
                        disabled(isButtonDisabled.data)
                        isButtonDisabled.data.render {
                            if (it) title("Please wait 5 seconds before trying again")
                            else title("Click to search")
                        }
                        className(isLoadingClassName.data)
                        searchButtonTextStore.data.render {
                            svg("fa-search")
                            +it
                        }
                        clicks handledBy { searchInner() }
                    }
                }
            }
        }
        div("mt-4") {
            inlineStyle("padding-left: 100px; padding-right:100px;")
            div("has-text-centered") {
                isLoading.data.render { loading -> if (loading) span("icon is-small") { +"Searching..." } }
            }
            resultStore.data.render { result ->
                when {
                    result == null -> {
                        div("has-text-centered has-text-grey mt-6") { +"Waiting for search..." }
                    }

                    result.data.isEmpty() -> {
                        div("has-text-centered has-text-grey mt-6") { +"No results found" }
                    }

                    else -> {
                        div("mb-3 has-text-weight-semibold is-flex is-justify-content-center is-align-items-center") {
                            +"Found "
                            span("has-text-danger mx-1") { +result.count.toString() }
                            +" results"
                        }
                        table("table is-striped is-hoverable is-fullwidth") {
                            thead {
                                tr {
                                    th("has-text-centered") { }
                                    th { }
                                    th {}
                                }
                            }
                            tbody {
                                result.data.forEach { artifact ->
                                    renderArtifactRow(artifact)
                                }
                            }
                        }
                    }
                }
            }

        }
    }
    coroutineScope.launchJob {
        val repoList = getRepositories()
        repoListStore.update(repoList)
        if (repoList.isNotEmpty()) selectedRepoStore.update(repoList.first().name)
        updateURI(keywordStore.current, repoList.first().name)
        searchInner()
    }
}

private fun updateURI(keyword: String, repo: String) {
    window.history.replaceState(null, "", "/#/search?k=${keyword}&r=${repo}")
}

private suspend fun search(
    keyword: String,
    repo: String,
    resultStore: Store<IndexSearchResponse?>,
    loadingStore: Store<Boolean>,
) {
    if (keyword.isBlank()) {
        resultStore.update(null)
        loadingStore.update(false)
        return
    }
    loadingStore.update(true)
    updateURI(keyword, repo)
    val response = httpRequest("${backendVersion.SEARCH_ARTIFACT}?k=$keyword&r=$repo")
        .acceptJson().jsonContentType().auth().get().arrayBuffer()
        .toByteArray().fromProtobuf<IndexSearchResponse>()
    if (response.code == 401) errorToast("You have no permission to search $repo")
    if (response.code == 499) errorToast("Keyword must be more than 3 characters.")
    resultStore.update(response)
    loadingStore.update(false)
}

private fun RenderContext.renderArtifactRow(artifact: IndexMetadata) {
    tr {
        td("has-text-centered") { +artifact.repo }
        td {
            val gav = "${artifact.repo}/" +
                    "${artifact.group.replace(".", "/").removePrefix("/")}/" +
                    "${artifact.artifact}/${artifact.version}"
            a {
                href("/#/$gav")
                +gav
            }
        }
        td {
            +formatDate(artifact.lastModified)
        }
    }
}
