/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/9 01:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages

import dev.fritz2.core.RenderContext
import dev.fritz2.core.alt
import dev.fritz2.core.href
import dev.fritz2.core.src

fun RenderContext.notFoundPage() {
    div("has-text-centered") {
        span("icon is-large has-text-info") {
            img {
                src("/assets/img/not-found.svg")
                alt("Not Found")
            }
        }
        h1("title is-3") {
            +"There's nothing here."
        }
        a("button is-danger") {
            +"Back to home page"
            href("/#/")
        }
    }
}