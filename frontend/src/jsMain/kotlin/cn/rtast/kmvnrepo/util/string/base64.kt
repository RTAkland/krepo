/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 17:07
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalEncodingApi::class)

package cn.rtast.kmvnrepo.util.string

import io.ktor.utils.io.core.toByteArray
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun String.toBase64(): String = Base64.encode(this.toByteArray())