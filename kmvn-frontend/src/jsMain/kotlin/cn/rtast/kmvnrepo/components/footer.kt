/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 13:24
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.components

import cn.rtast.kmvnrepo.frontendConfig
import cn.rtast.kmvnrepo.util.string.getCurrentYear
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.target

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
            val originCopyright = frontendConfig.copyright
                .replace("#YEAR#", getCurrentYear().toString())
            val targetLink = originCopyright.split("|")[1]
            div("column is-narrow has-text-right") {
                a("is-size-8") {
                    href(targetLink)
                    +originCopyright.split("|").first()
                    target("_blank")
                }
            }
        }
    }
}