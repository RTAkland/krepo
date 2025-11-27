/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:27 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.pages

import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import krepo.components.infoToast
import krepo.coroutineScope
import krepo.entity.RepositoryVisibility
import krepo.frontendConfig
import krepo.util.file.LocalStorage
import krepo.util.file.formatSize
import krepo.util.repo.getRepositories
import krepo.util.string.*

fun RenderContext.homePage() {
    val searchKeyword = storeOf("")
    coroutineScope.launch {
        val repositories = getRepositories()
        div("container") {
            inlineStyle("max-width: 50%")
            h1("title is-3 has-text-left mt-4 mb-2") { +"Repositories" }
            if (frontendConfig.description != null) {
                p("has-text-grey-dark mb-4") {
                    domNode.innerHTML = frontendConfig.description!!.castMarkdownToHtml()
                        .removePrefix("<body>").removeSuffix("</body>")
                }
            }
            table("table is-striped is-hoverable is-fullwidth") {
                caption { +"Repositories" }
                thead {
                    tr {
                        th { attr("style", "width: 30%") }
                        th("is-narrow") { attr("style", "width: 25%") }
                        if (LocalStorage.TOKEN != null) th("has-text-centered") { attr("style", "width: 25%") }
                        th("has-text-centered") { attr("style", "width: 25%") }
                    }
                }
                tbody {
                    repositories.forEach { repo ->
                        tr {
                            td {
                                a("has-text-link") {
                                    href("/#/${repo.name}")
                                    +repo.name
                                    title("Repo: ${repo.name} | Status: ${repo.status.name}")
                                }
                            }
                            td("is-narrow") {
                                span(
                                    "tag is-light${
                                        if (repo.visibility == RepositoryVisibility.Internal) " is-danger" else ""
                                    }"
                                ) {
                                    b { +repo.visibility.desc }
                                }
                            }
                            if (LocalStorage.TOKEN != null) td("has-text-centered") {
                                span("tag") { +formatSize(repo.size ?: 0L) }
                            }
                            td("has-text-centered") {
                                div("dropdown is-hoverable is-centered is-inline-block") {
                                    div("dropdown-trigger") {
                                        button("button is-small is-light") {
                                            i("fa-solid fa-copy") {}
                                            attr("aria-haspopup", "true")
                                            attr("aria-controls", "dropdown-menu-${repo.name}")
                                            clicks handledBy {
                                                window.navigator.clipboard.writeText(
                                                    getGradleKotlinDslRepositoryTemplate(repo.name)
                                                )
                                                infoToast("Gradle Kotlin DSL Repository URL Copied")
                                            }
                                        }
                                    }
                                    div("dropdown-menu") {
                                        id("dropdown-menu-${repo.name}")
                                        div("dropdown-content") {
                                            a("dropdown-item") {
                                                +"Gradle Kotlin DSL"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getGradleKotlinDslRepositoryTemplate(repo.name)
                                                    )
                                                    infoToast("Gradle Kotlin DSL Repository URL Copied")
                                                }
                                            }
                                            a("dropdown-item") {
                                                +"Gradle Groovy DSL"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getGradleGroovyDslRepositoryTemplate(repo.name)
                                                    )
                                                    infoToast("Gradle Groovy DSL Repository URL Copied")
                                                }
                                            }
                                            a("dropdown-item") {
                                                +"Maven"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getMavenRepositoryTemplate(repo.name)
                                                    )
                                                    infoToast("Maven Repository URL Copied")
                                                }
                                            }
                                            a("dropdown-item") {
                                                +"SBT"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getSBTRepositoryTemplate(repo.name)
                                                    )
                                                    infoToast("SBT Repository URL Copied")
                                                }
                                            }
                                        }
                                    }
                                }
//                                if (LocalStorage.TOKEN != null) {
//                                    div("dropdown is-hoverable is-centered is-inline-block ml-2") {
//                                        div("dropdown-trigger") {
//                                            button("button is-small is-light") {
//                                                i("fa-solid fa-magnifying-glass") {}
//                                                attr("aria-haspopup", "true")
//                                                attr("aria-controls", "dropdown-search-menu-${repo.name}")
//                                                clicks handledBy {
//                                                    if (searchKeyword.current.isBlank()) {
//                                                        warningToast("Please fill in the keyword to search!")
//                                                    } else {
//                                                        window.location.href =
//                                                            "/#/search?repo=${repo.name}&q=${searchKeyword.current}"
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        div("dropdown-menu") {
//                                            id("dropdown-search-menu-${repo.name}")
//                                            div("dropdown-content p-2") {

//                                            }
//                                        }
//                                    }
//                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

