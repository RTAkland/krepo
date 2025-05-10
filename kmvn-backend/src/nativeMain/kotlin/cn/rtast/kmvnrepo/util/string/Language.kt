/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/17 01:00
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util.string

import io.ktor.server.application.*
import io.ktor.server.request.*

fun ApplicationCall.getLanguage(): String {
    return this.request.acceptLanguage()?.lowercase()?.split(",")?.first() ?: "en-us"
}