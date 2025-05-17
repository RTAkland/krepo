/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalEncodingApi::class)
@file:Suppress("unused")

package cn.rtast.krepo.util.string

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun String.encodeToBase64(): String {
    return Base64.encode(this.encodeToByteArray())
}

fun ByteArray.encodeToBase64(): String {
    return Base64.encode(this)
}

fun ByteArray.decodeToByteArrayBase64(): ByteArray {
    return Base64.decode(this)
}

fun String.decodeToByteArrayBase64(): ByteArray {
    return Base64.decode(this)
}