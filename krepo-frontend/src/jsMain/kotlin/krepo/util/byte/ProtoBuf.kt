/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/3 20:00
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.util.byte

import krepo.util.fromProtobuf
import org.khronos.webgl.ArrayBuffer

inline fun <reified T> ArrayBuffer.fromProtoBuf(): T =
    this.toByteArray().fromProtobuf<T>()