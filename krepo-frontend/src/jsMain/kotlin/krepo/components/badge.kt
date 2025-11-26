/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:31 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.components

import dev.fritz2.core.RenderContext
import krepo.enums.BadgeType

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