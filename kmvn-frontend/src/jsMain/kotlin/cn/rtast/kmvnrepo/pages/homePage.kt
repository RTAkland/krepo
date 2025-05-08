/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 19:54
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages

import cn.rtast.kmvnrepo.components.infoToast
import cn.rtast.kmvnrepo.components.navbar
import cn.rtast.kmvnrepo.components.pageFooter
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.entity.GetRepositoriesResponse
import cn.rtast.kmvnrepo.frontendConfig
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.LocalStorage
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.string.*
import dev.fritz2.core.*
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
            .get().body().fromJson<GetRepositoriesResponse>().data.sortedBy { it.visibility }
        div("container") {
            h1("title is-2 has-text-centered mt-5") { +"仓库列表" }
            if (frontendConfig.description != null) {
                p("has-text-weight-bold mb-1 has-text-centered") {
                    +frontendConfig.description!!
                    hr("description-hr") {}
                }
            }
            div("columns is-multiline is-centered") {
                repositories.forEach { repo ->
                    div("column is-3") {
                        div("card") {
                            div("card-content") {
                                div("media") {
                                    div("media-left") {
                                        span("icon is-large has-text-primary") { i("fas fa-repository fa-2x") {} }
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
                                    i("fa-solid fa-eye mr-2") {}
                                    span { b { +"查看仓库" } }
                                }.tooltip { +"View the artifacts and files in this repository" }
                                a("card-footer-item has-text-link") {
                                    div("dropdown has-dropdown is-hoverable") {
                                        span {
                                            b {
                                                i("fa-solid fa-copy mr-2") {}
                                                +"复制仓库URL"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getGradleKotlinDslRepositoryTemplate(repo.name)
                                                    )
                                                    infoToast("已复制Gradle Kotlin DSL仓库URL")
                                                }
                                            }
                                            div("dropdown-menu") {
                                                id("dropdown-menu")
                                                attr("role", "menu")
                                                div("dropdown-content") {
                                                    a("dropdown-item") {
                                                        img("mr-2") {
                                                            width(14)
                                                            src("/assets/img/gradle_kotlin.svg")
                                                            alt("Gradle Kotlin DSL")
                                                        }
                                                        +"Gradle Kotlin DSL"
                                                        clicks handledBy {
                                                            window.navigator.clipboard.writeText(
                                                                getGradleKotlinDslRepositoryTemplate(repo.name)
                                                            )
                                                            infoToast("已复制Gradle Kotlin DSL仓库URL")
                                                        }
                                                    }
                                                    a("dropdown-item") {
                                                        img("mr-2") {
                                                            width(14)
                                                            src("/assets/img/gradle.svg")
                                                            alt("Gradle Groovy DSL")
                                                        }
                                                        +"Gradle Groovy DSL"
                                                        clicks handledBy {
                                                            window.navigator.clipboard.writeText(
                                                                getGradleGroovyDslRepositoryTemplate(repo.name)
                                                            )
                                                            infoToast("已复制Gradle Groovy DSL仓库URL")
                                                        }
                                                    }
                                                    a("dropdown-item") {
                                                        img("mr-2") {
                                                            width(14)
                                                            src("/assets/img/maven.svg")
                                                            alt("Maven")
                                                        }
                                                        +"Maven"
                                                        clicks handledBy {
                                                            window.navigator.clipboard.writeText(
                                                                getMavenRepositoryTemplate(repo.name)
                                                            )
                                                            infoToast("已复制Maven仓库URL")
                                                        }
                                                    }
                                                    a("dropdown-item") {
                                                        img("mr-2") {
                                                            width(14)
                                                            src("/assets/img/scala.svg")
                                                            alt("SBT")
                                                        }
                                                        +"SBT"
                                                        clicks handledBy {
                                                            window.navigator.clipboard.writeText(
                                                                getSBTRepositoryTemplate(repo.name)
                                                            )
                                                            infoToast("已复制SBT仓库URL")
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
                }
            }
            hr {}
        }
        pageFooter()
    }
}
