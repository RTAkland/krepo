/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/30 19:22
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.components.fa

import dev.fritz2.core.RenderContext
import dev.fritz2.core.src
import dev.fritz2.core.width
import krepo.isDarkTheme

fun RenderContext.svg(
    name: String,
    className: String = "mr-2",
    size: Int = 16,
) {
    img(className) faIcon@{
        inlineStyle("vertical-align: middle;")
        src("assets/img/fa/origin/${name.removePrefix("fa-")}.svg")
        width(size)
    }
}

fun RenderContext.svgm(
    name: String,
    className: String = "mr-2",
    size: Int = 16,
) {
    img(className) {
        inlineStyle("vertical-align: middle;")
        src("assets/img/fa/modified/${name.removePrefix("fa-")}.svg")
        width(size)
    }
}

fun RenderContext.svgBlock(
    name: String,
    className: String = "mr-2",
    size: Int = 16,
    block: RenderContext.() -> Unit = {},
) {
    img(className) faIcon@{
        inlineStyle("vertical-align: middle;")
        src("assets/img/fa/origin/${name.removePrefix("fa-")}.svg")
        width(size)
        this.block()
    }
}

fun RenderContext.autoSvg(
    name: String,
    className: String = "mr-2",
    size: Int = 16,
) {
    isDarkTheme.data.render {
        if (!it) svg(name, className, size) else svgm(name, className, size)
    }
}