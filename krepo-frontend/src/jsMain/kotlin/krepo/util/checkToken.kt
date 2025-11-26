/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:49 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalTime::class)

package krepo.util

import krepo.components.warningToast
import krepo.util.file.LocalStorage
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun checkToken() {
    val tokenExpireTimestamp = LocalStorage.EXPIRED_TIMESTAMP
    if (tokenExpireTimestamp != null) {
        val now = Clock.System.now().epochSeconds
        if (now >= tokenExpireTimestamp) {
            warningToast("The Session has expired.")
            LocalStorage.clearAll()
        }
    }
}