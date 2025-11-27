/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:30 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.pages.search

import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import krepo.backendVersion
import krepo.components.errorToast
import krepo.components.infoToast
import krepo.components.warningToast
import krepo.coroutineScope
import krepo.entity.ConfigRepositoryWithSize
import krepo.index.IndexMetadata
import krepo.index.IndexSearchResponse
import krepo.util.auth
import krepo.util.byte.toByteArray
import krepo.util.fromProtobuf
import krepo.util.httpRequest
import krepo.util.jsonContentType
import krepo.util.repo.getRepositories
import krepo.util.string.extractQueryParams

fun RenderContext.searchPage() {
    val queryParam = extractQueryParams(window.location.href)
    val keywordStore = storeOf(queryParam["k"] ?: "")
    val repoListStore = storeOf<List<ConfigRepositoryWithSize>>(emptyList())
    val selectedRepoStore = storeOf(queryParam["r"] ?: "")
    val resultStore = storeOf<IndexSearchResponse?>(null)
    val isButtonDisabled = storeOf(false)
    val isLoading = storeOf(false)
    div("container mt-5") {
        h2("title is-3 has-text-centered mb-5") { +"Search Artifacts" }
        div("box p-5 mx-auto") {
            inlineStyle("max-width: 900px;")
            div("columns is-vcentered") {
                div("column is-7 pr-1") {
                    input("input is-info is-medium") {
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
                                        +selected.ifEmpty { "Repository" }
                                    }
                                }
                                span("icon is-small") {
                                    i("fas fa-angle-down") {
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
                    button("button has-background-info-light is-fullwidth is-medium") {
                        disabled(isButtonDisabled.data)
                        isButtonDisabled.data.render {
                            if (it) title("Please wait 5 seconds before trying again")
                            else title("Click to search")
                        }
                        +"Search"
                        clicks handledBy {
                            if (!isButtonDisabled.current) {
                                isButtonDisabled.update(true)
                                coroutineScope.launch {
                                    if (keywordStore.current.length < 3) {
                                        errorToast("Keyword must more than 3 characters.")
                                    } else {
                                        infoToast("Searching...")
                                        search(keywordStore.current, selectedRepoStore.current, resultStore, isLoading)
                                        delay(5000)
                                    }
                                    isButtonDisabled.update(false)
                                }
                            } else warningToast("Please wait 5 seconds before trying again")
                        }
                    }
                }
            }
        }
        div("mt-4") {
            inlineStyle("padding-left: 100px; padding-right:100px;")
            div("has-text-centered") {
                isLoading.data.render { loading ->
                    if (loading) {
                        span("icon is-small") {
                            i("fas fa-spinner fa-spin") {}
                            +" Searching..."
                        }
                    }
                }
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
                        ul {
                            result.data.forEach { artifact ->
                                renderArtifactItem(artifact)
                            }
                        }
                    }
                }
            }
        }
    }
    coroutineScope.launch {
        val repoList = getRepositories()
        repoListStore.update(repoList)
        if (selectedRepoStore.current.isEmpty() && repoList.isNotEmpty()) {
            selectedRepoStore.update(repoList.first().name)
        }
        updateURI(keywordStore.current, repoList.first().name)
//        search(keywordStore.current, selectedRepoStore.current, resultStore, isLoading)
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
    if (response.code == 499) errorToast("Keyword must more than 3 characters.")
    resultStore.update(response)
    loadingStore.update(false)
}

private fun RenderContext.renderArtifactItem(artifact: IndexMetadata) {
    li("box mb-4") {
        div("is-flex is-justify-content-center is-align-items-center") {
            val gav = "${artifact.repo}/" +
                    "${artifact.group.replace(".", "/").removePrefix("/")}/" +
                    "${artifact.artifact}/${artifact.version}"
            a("is-size-5 has-text-weight-medium has-text-link") {
                href("/#/$gav")
                +gav
            }
        }
    }
}
