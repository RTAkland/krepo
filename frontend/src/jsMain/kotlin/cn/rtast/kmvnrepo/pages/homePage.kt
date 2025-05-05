/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 19:54
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages

import cn.rtast.kmvnrepo.components.infoToast
import cn.rtast.kmvnrepo.components.navbar
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.entity.GetRepositoriesResponse
import cn.rtast.kmvnrepo.getRepositoryTemplate
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.LocalStorage
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.string.fromJson
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.headless.components.tooltip
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.homePage() {
    navbar()
    coroutineScope.launch {
        val repositoriesAPIEndpoint =
            if (LocalStorage.TOKEN == null) "/@/api/repositories/public" else "/@/api/repositories/all"
        val repositories = httpRequest(repositoriesAPIEndpoint)
            .auth().acceptJson().jsonContentType()
            .get().body().fromJson<GetRepositoriesResponse>().data
        div("container") {
            hr {}
            h1("title is-2 has-text-centered mt-5") { +"仓库列表" }
            div("columns is-multiline is-centered") {
                repositories.forEach { repo ->
                    div("column is-3") {
                        div("card") {
                            div("card-content") {
                                div("media") {
                                    div("media-left") {
                                        span("icon is-large has-text-primary") {
                                            i("fas fa-repository fa-2x") {}
                                        }
                                    }
                                    div("media-content") {
                                        p("title is-4") {
                                            a {
                                                href("/#/${repo.name}")
                                                +repo.name
                                            }
                                        }
                                        p("subtitle is-6 has-text-grey") { +repo.visibility.desc }
                                    }
                                }
                            }
                            footer("card-footer") {
                                a("card-footer-item has-text-link") {
                                    href("/#/${repo.name}")
                                    +"\uD83D\uDC40 查看仓库"
                                }.tooltip { +"View the artifacts & files" }
                                a("card-footer-item has-text-link") {
                                    clicks handledBy {
                                        window.navigator.clipboard.writeText(getRepositoryTemplate(repo.name))
                                        infoToast("复制成功")
                                    }
                                    +"\uD83D\uDCCB 复制地址"
                                }.tooltip { +"Copy the maven repository config for gradle(Kotlin dsl)" }
                            }
                        }
                    }
                }
            }
            hr {}
        }
    }
}
