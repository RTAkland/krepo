/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 16:28
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.components

import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import krepo.components.fa.autoSvg
import krepo.components.fa.svg

fun RenderContext.showDialog(
    showDialog: Store<Boolean>,
    title: String,
    message: String? = null,
    content: RenderContext.() -> Unit,
    dialogAction: RenderContext.() -> Unit,
) = showDialog(showDialog, title, message, content, dialogAction, null)

fun RenderContext.showDialog(
    showDialog: Store<Boolean>,
    title: String,
    message: String? = null,
    content: RenderContext.() -> Unit,
    dialogAction: RenderContext.() -> Unit,
    extraButtons: (RenderContext.() -> Unit)?,
) {
    showDialog.data.render { visible ->
        if (visible) {
            div("modal is-active") {
                inlineStyle("z-index: 9999;")
                div("modal-background") {
                    clicks handledBy { showDialog.update(false) }
                }
                div("modal-card") {
                    header("modal-card-head") {
                        p("modal-card-title") { +title }
                        button("delete") {
                            attr("aria-label", "close")
                            clicks handledBy { showDialog.update(false) }
                        }
                    }
                    section("modal-card-body") {
                        if (message != null) p { +message }
                        content()
                    }
                    footer("modal-card-foot") {
                        div("buttons is-right") {
                            button("button is-right") {
                                autoSvg("fa-ban")
                                +"Cancel"
                                clicks handledBy { showDialog.update(false) }
                            }
                            button("button is-right") confirm@{
                                autoSvg("fa-check")
                                +"Confirm"
                                clicks handledBy {
                                    showDialog.update(false)
                                    dialogAction()
                                }
                            }
                            extraButtons?.invoke(this)
                        }
                    }
                }
            }
        }
    }
}
