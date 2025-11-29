/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:18 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.components

import dev.fritz2.headless.components.toast
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun warningToast(message: String, duration: Duration = 2.5.seconds, z: Int = -1) =
    toast("default", duration.inWholeMilliseconds) {
        div("notification is-warning is-light p-4 toast-message mb-2") {
            if (z > 0) inlineStyle("z-index: $z;")
            i("fas fa-exclamation-triangle mr-2") {}
            +message
        }
    }

fun errorToast(message: String, duration: Duration = 2.5.seconds, z: Int = -1) =
    toast("default", duration.inWholeMilliseconds) {
        div("notification is-danger is-light p-4 toast-message mb-2") {
            if (z > 0) inlineStyle("z-index: $z;")
            i("fas fa-ban mr-2") {}
            +message
        }
    }

fun infoToast(message: String, duration: Duration = 2.5.seconds, z: Int = -1) =
    toast("default", duration.inWholeMilliseconds) {
        div("notification is-light is-success p-4 toast-message mb-2") {
            if (z > 0) inlineStyle("z-index: $z;")
            i("fas fa-info-circle mr-2") {}
            +message
        }
    }
