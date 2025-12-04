/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:24 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.util

import dev.fritz2.remote.Request
import dev.fritz2.remote.http
import krepo.RouteEndpoint
import krepo.backend
import krepo.components.errorToast
import krepo.util.byte.toArrayBuffer
import krepo.util.file.LocalStorage

fun httpRequest(url: String): Request = http(backend + url)

fun httpRequest(url: RouteEndpoint): Request = http(backend + url.route)

fun Request.auth() = LocalStorage.TOKEN?.let { header("Authorization", "Bearer $it") } ?: this

fun Request.jsonContentType() = this.contentType("application/json")

/**
 * for azure functions
 */
inline fun <reified T> Request.body(body: T) = this.arrayBuffer(body.toProtobuf().toArrayBuffer())
    .header("Content-Type", "application/octet-stream")

inline fun <reified T> Request.setOctetBody(body: T) = body(body)

fun Request.octetType(): Request = header("Content-Type", "application/octet-stream")

fun Request.setStringBody(body: String) = this.body(body)

fun notImplToast() {
    errorToast("This API/page is not implemented yet.")
}