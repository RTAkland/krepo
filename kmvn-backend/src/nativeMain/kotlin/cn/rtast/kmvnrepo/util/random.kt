/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 20:50
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util

import kotlin.random.Random


fun generateSecureRandomString(length: Int = 50): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return buildString { repeat(length) { append(chars[Random.nextInt(chars.length)]) } }
}