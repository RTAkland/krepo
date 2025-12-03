/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:34 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import krepo.util.string.toJson

suspend inline fun <reified T> ApplicationCall.respondJson(content: T, code: Int = 200) {
    respondText(
        contentType = ContentType.Application.Json,
        text = content.toJson(),
        status = HttpStatusCode.fromValue(code)
    )
}