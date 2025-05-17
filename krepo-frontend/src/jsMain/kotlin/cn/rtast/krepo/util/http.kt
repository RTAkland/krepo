/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 15:18
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package cn.rtast.krepo.util

import cn.rtast.krepo.backend
import cn.rtast.krepo.util.file.LocalStorage
import cn.rtast.krepo.util.string.toJson
import dev.fritz2.remote.Request
import dev.fritz2.remote.http

fun httpRequest(url: String): Request = http(backend + url)

fun Request.auth() = LocalStorage.TOKEN?.let { header("Authorization", "Bearer $it") } ?: this

fun Request.jsonContentType() = this.contentType("application/json")

inline fun <reified T> Request.body(body: T) = this.body(body.toJson())

inline fun <reified T> Request.setBody(body: T) = body(body)