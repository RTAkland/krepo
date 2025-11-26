/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:59 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.HttpStatus
import cn.rtast.kazure.response.respondText
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import krepo.azure.NOT_IMPLEMENTED_TEXT

val client = HttpClient(OkHttp)

fun <T> HttpRequest<T>.notImplemented(): HttpResponse =
    this.respondText(NOT_IMPLEMENTED_TEXT, status = HttpStatus.NOT_IMPLEMENTED)