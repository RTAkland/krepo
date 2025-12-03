/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:27 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.pages

import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.storeOf
import dev.fritz2.core.title
import kotlinx.browser.window
import krepo.components.fa.svg
import krepo.components.infoToast
import krepo.coroutineScope
import krepo.entity.maven.ConfigRepositoryWithSize
import krepo.entity.maven.RepositoryVisibility
import krepo.frontendConfig
import krepo.isDarkTheme
import krepo.util.file.LocalStorage
import krepo.util.file.formatSize
import krepo.util.launchJob
import krepo.util.repo.getRepositories
import krepo.util.string.getGradleKotlinDslRepositoryTemplate

fun RenderContext.HomePage(repos: List<ConfigRepositoryWithSize>?) {
    val repositories = storeOf(repos ?: emptyList())
    if (repositories.current.isEmpty()) coroutineScope.launchJob { repositories.update(getRepositories()) }
    div("container app-navbar") {
        inlineStyle("max-width: 50%")
        div("mt-5 is-flex is-align-items-start") {
            if (frontendConfig.description != null) span {
                domNode.innerHTML = frontendConfig.description!!
            } else span { +"Maven repository" }
        }
        table("table is-striped is-hoverable is-fullwidth mt-5") {
            thead {
                tr {
                    th("has-text-centered") { attr("style", "width: 30%") }
                    th("is-narrow has-text-centered") { attr("style", "width: 25%") }
                    if (LocalStorage.TOKEN != null) th("has-text-centered") { attr("style", "width: 25%") }
                    th("has-text-centered") { attr("style", "width: 25%") }
                }
            }
            tbody {
                repositories.data.renderEach { repo ->
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
                                        isDarkTheme.data.render {
                                            if (!it) {
                                                className("is-light")
                                                svg("fa-copy", "", 12)
                                            } else svg("fa-copy-light", "", 12)
                                        }
                                        attr("aria-haspopup", "true")
                                        attr("aria-controls", "dropdown-menu-${repo.name}")
                                        clicks handledBy {
                                            window.navigator.clipboard.writeText(
                                                getGradleKotlinDslRepositoryTemplate(repo.name)
                                            )
                                            infoToast("Gradle(kts) repo config copied")
                                        }
                                        title("Copy Gradle Kotlin DSL repository config block")
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
}

