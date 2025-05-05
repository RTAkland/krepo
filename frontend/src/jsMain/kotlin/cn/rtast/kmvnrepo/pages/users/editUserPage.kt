/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 21:44
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages.users

import cn.rtast.kmvnrepo.components.navbar
import cn.rtast.kmvnrepo.currentPath
import cn.rtast.kmvnrepo.util.file.checkSession
import dev.fritz2.core.RenderContext

fun RenderContext.editUserPage() {
    checkSession {
        navbar()
        val user = currentPath.split("?").last().split("&").first().split("=").last()
        println(user)
    }
}