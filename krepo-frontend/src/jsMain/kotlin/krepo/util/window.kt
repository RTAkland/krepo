/*
 * Copyright © 2025 RTAkland
 * Date: 9/23/25, 1:42 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.util

import kotlinx.browser.window
import krepo.developmentMode

fun getCurrentHttpUrl(): String {
    val protocol = window.location.protocol
    val host = window.location.host
    return "$protocol//$host"
}

val customBacked
    get() = if (!developmentMode) "https://repo.maven.rtast.cn"
    else if (developmentMode) "http://127.0.0.1:7071" else getCurrentHttpUrl()

fun navTo(
    target: String,
    args: Map<String, Any?> = mapOf(),
    reload: Boolean = false,
) {
    val t = args.map { "${it.key}=${it.value.toString()}" }.joinToString("&")
    window.location.href = target + if (args.isNotEmpty()) "?$t" else ""
    if (reload) window.location.reload()
}