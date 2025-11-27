/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/13 22:47
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.util.byte

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array

fun ArrayBuffer.toByteArray() = Int8Array(this).unsafeCast<ByteArray>()