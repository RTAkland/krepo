/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:18 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.components

import dev.fritz2.headless.components.toast
import krepo.util.img.autoSvg
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun warningToast(message: String, duration: Duration = 2.5.seconds) =
    toast("default", duration.inWholeMilliseconds) {
        div("notification p-4 toast-message mb-2") { autoSvg("fa-triangle-exclamation", size = 13);+message }
    }

fun errorToast(message: String, duration: Duration = 2.5.seconds) =
    toast("default", duration.inWholeMilliseconds) {
        div("notification p-4 toast-message mb-2") { autoSvg("fa-xmark", size = 13);+message }
    }

fun infoToast(message: String, duration: Duration = 2.5.seconds) =
    toast("default", duration.inWholeMilliseconds) {
        div("notification p-4 toast-message mb-2") { autoSvg("fa-info-circle", size = 13   );+message }
    }
