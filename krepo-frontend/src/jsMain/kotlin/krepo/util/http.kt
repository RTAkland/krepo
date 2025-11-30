/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:24 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.util

import dev.fritz2.core.RenderContext
import dev.fritz2.remote.Request
import dev.fritz2.remote.Response
import dev.fritz2.remote.http
import krepo.RouteEndpoint
import krepo.backend
import krepo.components.errorToast
import krepo.enums.CheckImplType
import krepo.pages.other.NotImplPage
import krepo.renderContext
import krepo.util.file.LocalStorage

fun httpRequest(url: String): Request = http(backend + url)

fun httpRequest(url: RouteEndpoint): Request = http(backend + url.route)

fun Request.auth() = LocalStorage.TOKEN?.let { header("Authorization", "Bearer $it") } ?: this

fun Request.jsonContentType() = this.contentType("application/json")

inline fun <reified T> Request.body(body: T) = this.body(body.toJson())

inline fun <reified T> Request.setBody(body: T) = body(body)

fun Response.checkImpl(type: CheckImplType = CheckImplType.Page, block: RenderContext.() -> Unit = {}): Response {
    if (this.status == 501) {
        when (type) {
            CheckImplType.Page -> renderContext.NotImplPage()
            CheckImplType.Toast -> errorToast("This API/page is not implemented yet.")
        }
    } else renderContext.block()
    return this
}