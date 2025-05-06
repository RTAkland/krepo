/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 19:52
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages

import cn.rtast.kmvnrepo.components.pageFooter
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import kotlinx.browser.window
import kotlin.time.Duration

fun RenderContext.noPermission(redirect: String, duration: Duration) {
    window.setTimeout({ window.location.href = redirect }, duration.inWholeMilliseconds.toInt())
    div("container has-text-centered mt-6") {
        div("box") {
            span("icon is-large has-text-danger") { i("fas fa-ban fa-3x") {} }
            h1("title is-3 mt-4") { +"访问被拒绝" }
            p("subtitle is-5") { +"你没有权限访问此页面" }
            p("subtitle is-5") { +"你将在3秒后被重定向到 $redirect" }
            a("button is-danger mt-4") {
                +"返回主页"
                href("/#/")
            }
        }
    }
    pageFooter()
}