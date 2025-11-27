/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:31 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.components

import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.target
import krepo.frontendConfig
import krepo.util.string.getCurrentYear

fun RenderContext.pageFooter() {
    div("page-footer-icp") {
        div("footer-inner columns is-mobile is-vcentered is-multiline") {
            div("column is-flex-grow-1 has-text-right") {
                if (frontendConfig.icpLicense != null) {
                    a {
                        href("https://beian.miit.gov.cn/")
                        +frontendConfig.icpLicense!!
                        target("_blank")
                    }
                }
            }
            val copyrightInfo = frontendConfig.copyright
                .replace("#YEAR#", getCurrentYear().toString())
                .removePrefix("<body>").removeSuffix("</body>")
            div("column is-narrow has-text-right") { span("is-size-8") { domNode.innerHTML = copyrightInfo } }
        }
    }
}