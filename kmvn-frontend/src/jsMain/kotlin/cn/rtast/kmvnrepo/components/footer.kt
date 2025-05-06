/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 13:24
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.components

import cn.rtast.kmvnrepo.frontendConfig
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.target

fun RenderContext.pageFooter() {
    if (frontendConfig.icpLicense != null) {
        div("content has-text-centered page-footer-icp") {
            a {
                href("https://beian.miit.gov.cn/")
                +frontendConfig.icpLicense!!
                target("_blank")
            }
        }
    }
}