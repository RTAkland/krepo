/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/15 02:37
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages.search

import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.entity.SearchArtifactResponse
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.checkSession
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.string.extractQueryParams
import cn.rtast.kmvnrepo.util.string.fromJson
import dev.fritz2.core.RenderContext
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.searchPage() {
    checkSession {
        val queryParam = extractQueryParams(window.location.href)
        val repo = queryParam["repo"]!!
        val keyword = queryParam["q"]!!
        coroutineScope.launch {
            val response = httpRequest("/@/api/artifacts/search/$repo?name=$keyword")
                .auth().acceptJson().jsonContentType()
                .get().body().fromJson<SearchArtifactResponse>()
            div("container mt-5") {
                h2("title is-3 has-text-centered mb-4") { +"Search results" }
                inlineStyle("max-width: 60%;")
                div("box") {
                    if (response.count == 0) {
                        div("has-text-centered has-text-grey") { +"Nothing found" }
                    } else {
                        div("mb-3 has-text-weight-semibold") { +"Found ${response.count} results" }
                        ul {
                            response.data.forEach { artifact ->
                                renderArtifactItem(repo, artifact)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun RenderContext.renderArtifactItem(repo: String, artifact: SearchArtifactResponse.SearchArtifact) {
    li("box mb-3") {
        div("is-flex is-justify-content-space-between is-align-items-center") {
            div("is-size-5 has-text-weight-medium") {
                +"${artifact.group}:${artifact.artifactId}"
            }
        }
        div("mt-2") {
            span("tags") {
                +"Versions: "
                artifact.versions.forEach { version ->
                    a("tag is-info is-light mr-1 is-link") {
                        +version
                        clicks handledBy {
                            window.location.href = "/#/$repo/${artifact.groupSlashed}/${artifact.artifactId}/$version"
                        }
                    }
                }
            }
        }
    }
}
