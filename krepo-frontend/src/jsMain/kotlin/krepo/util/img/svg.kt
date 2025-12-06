/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/30 19:22
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.util.img

import dev.fritz2.core.RenderContext
import dev.fritz2.core.src
import dev.fritz2.core.width
import krepo.isDarkTheme

private fun RenderContext.svgd(
    name: String,
    className: String = "mr-2",
    size: Int = 16,
) {
    img(className) faIcon@{
        inlineStyle("vertical-align: middle;")
        src("assets/img/dark/${name.removePrefix("fa-")}.svg")
        width(size)
    }
}

private fun RenderContext.svgl(
    name: String,
    className: String = "mr-2",
    size: Int = 16,
) {
    img(className) {
        inlineStyle("vertical-align: middle;")
        src("assets/img/light/${name.removePrefix("fa-")}.svg")
        width(size)
    }
}

fun RenderContext.autoSvg(
    name: String,
    className: String = "mr-2",
    size: Int = 16,
) {
    isDarkTheme.data.render {
        if (!it) svgd(name, className, size) else svgl(name, className, size)
    }
}