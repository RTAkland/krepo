/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:31 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.components

import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.target
import krepo.frontendConfig
import krepo.util.string.getCurrentYear

fun RenderContext.PageFooter() {
    div("page-footer-icp") {
        div("footer-inner columns is-mobile is-vcentered is-multiline") {
            a { href("/#/terms?l=en-us"); inlineStyle("margin-left: 30px; text-decoration: underline;"); +"Terms" }
            span("mx-1") { +"·" }
            a { href("/#/privacy?l=en-us"); inlineStyle("text-decoration: underline;"); +"Privacy" }
            span("mx-1") { +"·" }
            a { href("/#/licenses"); inlineStyle("text-decoration: underline;"); +"Licenses" }
            // right
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
            div("column is-narrow has-text-right") { span("is-size-8") { domNode.innerHTML = copyrightInfo } }
        }
    }
}