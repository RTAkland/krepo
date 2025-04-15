/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 21:16
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package cn.rtast.kmvnrepo.util

//import kotlinx.browser.window
//import kotlinx.coroutines.await
//import org.khronos.webgl.ArrayBuffer
//import org.khronos.webgl.Int8Array
//import org.w3c.fetch.Response
//
//suspend fun loadRes(url: String): ArrayBuffer {
//    return window.fetch(url).await<Response>().arrayBuffer().await()
//}
//
//fun ArrayBuffer.toByteArray(): ByteArray {
//    val source = Int8Array(this, 0, byteLength)
//    return jsInt8ArrayToKotlinByteArray(source)
//}
//
//
//@JsFun(
//    """ (src, size, dstAddr) => {
//        const mem8 = new Int8Array(wasmExports.memory.buffer, dstAddr, size);
//        mem8.set(src);
//    }
//"""
//)
//external fun jsExportInt8ArrayToWasm(src: Int8Array, size: Int, dstAddr: Int)
//
//internal fun jsInt8ArrayToKotlinByteArray(x: Int8Array): ByteArray {
//    val size = x.length
//
//    return withScopedMemoryAllocator { allocator ->
//        val memBuffer = allocator.allocate(size)
//        val dstAddress = memBuffer.address.toInt()
//        jsExportInt8ArrayToWasm(x, size, dstAddress)
//        ByteArray(size) { i -> (memBuffer + i).loadByte() }
//    }
//}