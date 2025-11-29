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
import krepo.isDarkTheme
import krepo.util.file.LocalStorage
import krepo.util.file.formatSize
import krepo.util.repo.getRepositories
import krepo.util.string.getGradleKotlinDslRepositoryTemplate
import krepo.util.string.getMavenRepositoryTemplate

fun RenderContext.homePage() {
    coroutineScope.launch {
        val repositories = getRepositories()
        div("container") {
            inlineStyle("max-width: 50%")
            h1("title is-3 has-text-left mt-4 mb-2") { +"Repositories" }
            if (frontendConfig.description != null) {
                p("mb-4") {
                    domNode.innerHTML = frontendConfig.description!!
                        .removePrefix("<body>").removeSuffix("</body>")
                }
            }
            table("table is-striped is-hoverable is-fullwidth") {
                caption { +"Repositories" }
                thead {
                    tr {
                        th("has-text-centered") { attr("style", "width: 30%") }
                        th("is-narrow has-text-centered") { attr("style", "width: 25%") }
                        if (LocalStorage.TOKEN != null) th("has-text-centered") { attr("style", "width: 25%") }
                        th("has-text-centered") { attr("style", "width: 25%") }
                    }
                }
                tbody {
                    repositories.forEach { repo ->
                        tr {
                            td("has-text-centered") {
                                a {
                                    href("/#/${repo.name}")
                                    +repo.name
                                    title("Repo: ${repo.name} | Status: ${repo.status.name}")
                                }
                            }
                            td("is-narrow has-text-centered") {
                                span("tag") {
                                    isDarkTheme.data.render { if (!it) className("is-light") }
                                    b { +repo.visibility.desc }
                                    if (repo.visibility == RepositoryVisibility.Internal)
                                        title("This is a private repository, credential is required when access.")
                                    else title("This is a public repository, no credential is required with access.")
                                }
                            }
                            if (LocalStorage.TOKEN != null) td("has-text-centered") {
                                span("tag") {
                                    +formatSize(repo.size ?: 0L)
                                    title("Repository size: ${repo.size ?: "Unknown size"} (Byte)")
                                }
                            }
                            td("has-text-centered") {
                                div("dropdown is-hoverable is-centered is-inline-block") {
                                    div("dropdown-trigger") {
                                        button("button is-small") {
                                            isDarkTheme.data.render { if (!it) className("is-light") }
                                            i("fa-solid fa-copy") {}
                                            attr("aria-haspopup", "true")
                                            attr("aria-controls", "dropdown-menu-${repo.name}")
                                            clicks handledBy {
                                                window.navigator.clipboard.writeText(
                                                    getGradleKotlinDslRepositoryTemplate(repo.name)
                                                )
                                                infoToast("Gradle Kotlin DSL Repository URL Copied")
                                            }
                                            title("Copy Gradle Kotlin DSL repository config (default)")
                                        }
                                    }
                                    div("dropdown-menu") {
                                        id("dropdown-menu-${repo.name}")
                                        div("dropdown-content") {
                                            a("dropdown-item") {
                                                img { src("assets/img/Kotlin Multiplatform icon.svg");width(12) }
                                                +"Gradle Kotlin DSL"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getGradleKotlinDslRepositoryTemplate(repo.name)
                                                    )
                                                    infoToast("Gradle Kotlin DSL Repository URL Copied")
                                                }
                                                title("Copy Gradle Kotlin DSL repository config")
                                            }
                                            a("dropdown-item") {
                                                img { src("assets/img/maven.svg");width(12) }
                                                +"Maven"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getMavenRepositoryTemplate(repo.name)
                                                    )
                                                    infoToast("Maven Repository URL Copied")
                                                }
                                                title("Copy Maven repository config")
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

