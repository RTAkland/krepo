/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:26 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.util.file

import dev.fritz2.core.RenderContext
import krepo.pages.other.noPermission

fun RenderContext.checkPermission(
    content: RenderContext.() -> Unit,
) = if (LocalStorage.TOKEN != null) content() else noPermission()
