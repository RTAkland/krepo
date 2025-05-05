/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 17:29
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalStdlibApi::class)

package cn.rtast.kmvnrepo.util.file

import org.kotlincrypto.hash.md.MD5

fun md5(input: String): String =
    MD5().digest(input.encodeToByteArray()).toHexString()
