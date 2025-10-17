/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:49
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.util

import cn.rtast.krepo.util.string.toJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend inline fun <reified T> ApplicationCall.respondJson(content: T, code: Int = 200) {
    respondText(
        contentType = ContentType.Application.Json,
        text = content.toJson(),
        status = HttpStatusCode.fromValue(code)
    )
}