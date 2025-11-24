/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/7 20:10
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.components

import cn.rtast.krepo.enums.BadgeType
import dev.fritz2.core.RenderContext

fun RenderContext.badge(
    type: BadgeType,
    extraContent: RenderContext.() -> Unit,
): RenderContext {
    span("mr-2 tag ${type.classes} is-light") {
        extraContent()
        +type.chinese
    }
    return this
}