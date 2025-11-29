/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:53 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.pages.other

import dev.fritz2.core.RenderContext
import kotlinx.browser.window

fun RenderContext.notImplPage() {
    div("container not-impl-container") {
        div("w-full h-full flex flex-col justify-center items-center text-gray-600 p-8") {
            div("text-center max-w-md") {
                repeat(4) { br {} }
                h2("title is-2 text-3xl font-bold mb-2") { +"Not implemented page" }
                p("text-lg text-gray-500 mb-6") { +"This page/API is not implemented yet" }
                button("button px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-xl shadow transition") {
                    +"Back to home page"
                    clicks handledBy { window.location.href = "/" }
                }
            }
        }
    }
}