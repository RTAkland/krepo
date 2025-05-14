///*
// * Copyright © 2025 RTAkland
// * Date: 2025/5/15 02:37
// * Open Source Under Apache-2.0 License
// * https://www.apache.org/licenses/LICENSE-2.0
// */
//
//package cn.rtast.kmvnrepo.pages.search
//
//import dev.fritz2.core.RenderContext
//import dev.fritz2.core.storeOf
//import kotlinx.browser.window
//
//fun RenderContext.searchPage() {
//    val queryParam = window.location.href.substringAfter("q=", "").substringBefore("&")
//    val searchQuery = storeOf(queryParam)
//    val results = storeOf<List<ArtifactSearchResult>>(emptyList())
//    val isLoading = storeOf(false)
//
//    div("section") {
//        div("container") {
//            h1("title is-4") { +"Search Results for: \"${searchQuery.current}\"" }
//
//            isLoading.render {
//                if (it) {
//                    p { +"Loading results..." }
//                }
//            }
//
//            results.data.renderEach {
//                div("box") {
//                    h2("subtitle is-6") { +"${it.group}:${it.artifact}:${it.version}" }
//                    a("button is-small is-link") {
//                        +"View"
//                        attr("href", "/#/artifact/view?group=${it.group}&artifact=${it.artifact}&version=${it.version}")
//                    }
//                }
//            }
//
//            results.data.render {
//                if (it.isEmpty()) {
//                    p("has-text-grey") { +"No results found." }
//                }
//            }
//        }
//    }
//
//    coroutineScope.launch {
//        if (queryParam.isNotBlank()) {
//            isLoading.update(true)
//            val http = httpRequest("/@/api/artifact/search?q=${queryParam}")
//                .auth()
//                .acceptJson()
//                .get()
//
//            if (http.ok) {
//                val data = http.body().fromJson<List<ArtifactSearchResult>>()
//                results.update(data)
//            }
//            isLoading.update(false)
//        }
//    }
//}