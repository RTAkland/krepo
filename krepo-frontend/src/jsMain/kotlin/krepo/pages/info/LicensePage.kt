/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/30 13:46
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")


package krepo.pages.info

import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import kotlinx.coroutines.launch
import krepo.backendVersion
import krepo.coroutineScope
import krepo.entity.system.info.AppInfoContent
import krepo.util.fromJson
import krepo.util.httpRequest
import krepo.util.launchJob

fun RenderContext.LicensePage() {
    val licenseContents = storeOf<AppInfoContent?>(null)
    renderContentPage(
        title = "Licenses",
        content = licenseContents,
    )
    coroutineScope.launchJob {
        val resp = httpRequest(backendVersion.LICENSES)
            .get().body().fromJson<AppInfoContent>()
        licenseContents.update(resp)
    }
}