/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 15:18
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package cn.rtast.kmvnrepo.util

import cn.rtast.kmvnrepo.backend
import dev.fritz2.remote.Request
import dev.fritz2.remote.http

fun httpRequest(url: String): Request = http(backend + url)

fun Request.auth() = header("Authorization", "Bearer ${LocalStorage.TOKEN!!}")

fun Request.jsonContentType() = this.contentType("application/json")

inline fun <reified T> Request.body(body: T) = this.body(body.toJson())

inline fun <reified T> Request.setBody(body: T) = body(body)