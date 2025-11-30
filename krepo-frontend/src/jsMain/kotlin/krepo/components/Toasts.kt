/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:18 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.components

import dev.fritz2.headless.components.toast
import krepo.components.fa.svg
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun warningToast(message: String, duration: Duration = 2.5.seconds) =
    toast("default", duration.inWholeMilliseconds) {
        div("notification is-light p-4 toast-message mb-2") { svg("fa-triangle-exclamation");+message }
    }

fun errorToast(message: String, duration: Duration = 2.5.seconds) =
    toast("default", duration.inWholeMilliseconds) {
        div("notification is-light p-4 toast-message mb-2") { svg("fa-xmark");+message }
    }

fun infoToast(message: String, duration: Duration = 2.5.seconds) =
    toast("default", duration.inWholeMilliseconds) {
        div("notification is-light p-4 toast-message mb-2") { svg("fa-info-circle");+message }
    }
