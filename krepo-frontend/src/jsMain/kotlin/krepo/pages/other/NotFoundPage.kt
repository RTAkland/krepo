/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/10 14:26
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.pages.other

import dev.fritz2.core.RenderContext
import krepo.util.img.autoFASvg
import krepo.util.navTo

fun RenderContext.NotFoundPage() {
    div("has-text-centered") {
        autoFASvg("fa-ban", "", 128)
        h1("title is-3") { +"There's nothing here." }
        a("button is-danger") {
            +"Back to home page"
            clicks handledBy { navTo("/") }
        }
    }
}