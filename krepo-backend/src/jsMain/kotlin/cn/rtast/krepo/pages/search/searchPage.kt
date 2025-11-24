/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/15 02:37
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.pages.search

import cn.rtast.krepo.backend
import cn.rtast.krepo.backendVersion
import cn.rtast.krepo.coroutineScope
import cn.rtast.krepo.util.auth
import cn.rtast.krepo.util.file.checkSession
import cn.rtast.krepo.util.httpRequest
import cn.rtast.krepo.util.jsonContentType
import cn.rtast.krepo.util.string.extractQueryParams
import cn.rtast.krepo.util.string.fromJson
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.storeOf
import kotlinx.browser.window
import kotlinx.coroutines.launch
import krepo.entity.V2ArtifactSearchResponse

fun RenderContext.searchPage() {
    checkSession {
        val queryParam = extractQueryParams(window.location.href)
        val keyword = queryParam["q"] ?: ""
        val responseFlow = storeOf<V2ArtifactSearchResponse?>(null)
        div("container mt-5") {
            h2("title is-3 has-text-centered mb-4") { +"Search results" }
            div("box") {
                inlineStyle("max-width: 60%; margin: 0 auto;")
                responseFlow.data.render { response ->
                    if (response == null) {
                        div("has-text-centered has-text-grey") { +"Loading..." }
                    } else if (response.data.isEmpty()) {
                        div("has-text-centered has-text-grey") { +"Nothing found" }
                    } else {
                        div("mb-3 has-text-weight-semibold") { +"Found ${response.data.size} results" }
                        ul {
                            response.data.forEach { artifact ->
                                renderArtifactItem(artifact)
                            }
                        }
                    }
                }
            }
        }
        coroutineScope.launch {
            val response = httpRequest("${backendVersion.V2_SEARCH_ARTIFACT}?keyword=$keyword")
                .acceptJson().jsonContentType().auth()
                .get().body().fromJson<V2ArtifactSearchResponse>()
            responseFlow.update(response)
        }
    }
}

private fun RenderContext.renderArtifactItem(artifact: V2ArtifactSearchResponse.ArtifactSearch) {
    li("box mb-3") {
        div("is-flex is-justify-content-space-between is-align-items-center") {
            div("is-size-5 has-text-weight-medium") {
                a("is-link is-info is-light") {
                    if (artifact.isFile) href("$backend/${artifact.path}")
                    else href("/#/${artifact.path}")
                    +artifact.path
                }
            }
        }
    }
}