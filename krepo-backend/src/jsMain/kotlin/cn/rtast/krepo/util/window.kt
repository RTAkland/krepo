/*
 * Copyright © 2025 RTAkland
 * Date: 9/23/25, 1:42 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.util

import kotlinx.browser.window

fun getCurrentHttpUrl(): String {
    val protocol = window.location.protocol
    val host = window.location.host
    return "$protocol//$host"
}