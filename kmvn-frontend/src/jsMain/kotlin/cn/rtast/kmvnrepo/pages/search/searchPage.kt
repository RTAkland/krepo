/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/15 02:37
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages.search

import cn.rtast.kmvnrepo.backend
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.entity.V2ArtifactSearchResponse
import cn.rtast.kmvnrepo.util.file.checkSession
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.string.extractQueryParams
import cn.rtast.kmvnrepo.util.string.fromJson
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.storeOf
import kotlinx.browser.window
import kotlinx.coroutines.launch

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
            val response = httpRequest("/@/api/v2/artifacts/search?keyword=$keyword")
                .acceptJson().jsonContentType()
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