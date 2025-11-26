/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 16:07
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.components

import dev.fritz2.headless.components.toast
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun warningToast(message: String, duration: Duration = 2.5.seconds) = toast("default", duration.inWholeMilliseconds) {
    div("notification is-warning is-light p-4 toast-message mb-2") {
        i("fas fa-exclamation-triangle mr-2") {}
        +message
    }
}

fun errorToast(message: String, duration: Duration = 2.5.seconds) = toast("default", duration.inWholeMilliseconds) {
    div("notification is-danger is-light p-4 toast-message mb-2") {
        i("fas fa-ban mr-2") {}
        +message
    }
}

fun infoToast(message: String, duration: Duration = 2.5.seconds) = toast("default", duration.inWholeMilliseconds) {
    div("notification is-info is-light p-4 toast-message mb-2") {
        i("fas fa-info-circle mr-2") {}
        +message
    }
}
