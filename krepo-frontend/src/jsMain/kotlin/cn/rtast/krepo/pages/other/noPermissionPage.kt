/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/10 14:26
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.pages.other

import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import kotlinx.browser.window
import kotlin.time.Duration.Companion.seconds

fun RenderContext.noPermission() {
    window.setTimeout({ window.location.href = "/#/" }, 5.seconds.inWholeMilliseconds.toInt())
    div("container has-text-centered mt-6") {
        div("box") {
            span("icon is-large has-text-danger") { i("fas fa-ban fa-3x") {} }
            h1("title is-3 mt-4") { +"Access Denied" }
            p("subtitle is-5") { +"You Do Not have permission to view this page" }
            p("subtitle is-5") { +"You will be redirect to home page in 5 seconds" }
            a("button is-danger mt-4") {
                +"Back to home page"
                href("/#/")
            }
        }
    }
}