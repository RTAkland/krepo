/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/29 20:33
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.pages.info

import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import krepo.entity.system.info.AppInfoContent

fun RenderContext.renderContentPage(
    title: String,
    content: Store<AppInfoContent?>,
) {
    div("container") {
        div("box p-5 my-5") {
            h2("title is-2 has-text-centered") { +title }
            hr("my-4") {}
            div {
                inlineStyle("font-size: 1.1em; line-height: 1.8; padding-top: 20px; padding-bottom: 20px;")
                content.data.render {
                    if (it != null) {
                        div("content has-text-justified") {
                            domNode.innerHTML = content.current?.content!!
                        }
                    } else {
                        div("has-text-centered has-text-grey-dark") { +"Loading..." }
                    }
                }
            }
        }
    }
}

