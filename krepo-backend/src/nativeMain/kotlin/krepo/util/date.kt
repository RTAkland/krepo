/*
 * Copyright © 2025 RTAkland
 * Date: 9/26/25, 9:49 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalTime::class, ExperimentalForeignApi::class)

package krepo.util

import kotlinx.cinterop.*
import platform.posix.*
import kotlin.time.ExperimentalTime

fun getMavenLastUpdated(): String = memScoped {
    val t = alloc<time_tVar>()
    t.value = time(null)
    val tmStruct = alloc<tm>()
    gmtime_r(t.ptr, tmStruct.ptr)
    val buffer = allocArray<ByteVar>(32)
    strftime(buffer, 32.convert(), "%Y%m%d%H%M%S", tmStruct.ptr)
    buffer.toKString()
}