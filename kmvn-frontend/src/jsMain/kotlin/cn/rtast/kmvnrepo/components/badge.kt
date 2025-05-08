/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/7 20:10
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.components

import dev.fritz2.core.RenderContext

fun RenderContext.badge(
    text: String, classes: String,
    extraContent: RenderContext.() -> Unit
): RenderContext {
    span("$classes mr-2") {
        extraContent()
        +text
    }
    return this
}