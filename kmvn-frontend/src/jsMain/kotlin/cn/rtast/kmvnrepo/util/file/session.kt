/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 19:51
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util.file

import cn.rtast.kmvnrepo.pages.other.noPermission
import dev.fritz2.core.RenderContext

fun RenderContext.checkSession(
    content: RenderContext.() -> Unit
) = if (LocalStorage.TOKEN != null) content() else noPermission()
