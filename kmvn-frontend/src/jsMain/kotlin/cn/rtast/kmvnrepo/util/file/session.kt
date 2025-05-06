/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 19:51
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util.file

import cn.rtast.kmvnrepo.pages.noPermission
import dev.fritz2.core.RenderContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun RenderContext.checkSession(
    redirect: String = "/#/",
    duration: Duration = 3.seconds,
    content: RenderContext.() -> Unit
) = if (LocalStorage.TOKEN != null) content() else noPermission(redirect, duration)
