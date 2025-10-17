/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.util.string

import io.ktor.server.application.*
import io.ktor.server.request.*

fun ApplicationCall.getLanguage(): String {
    return this.request.acceptLanguage()?.lowercase()?.split(",")?.first() ?: "en-us"
}