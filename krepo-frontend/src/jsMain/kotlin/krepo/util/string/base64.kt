/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 10:51 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.util.string

import kotlin.io.encoding.Base64

fun String.encodeToBase64(): String {
    return Base64.encode(this.encodeToByteArray())
}

fun ByteArray.encodeToBase64(): String {
    return Base64.encode(this)
}

fun String.decodeToString(): String {
    return Base64.decode(this).decodeToString()
}

fun String.decodeToByteArray(): ByteArray {
    return Base64.decode(this)
}