/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 22:59
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util

fun Double.round(size: Int = 2): String {
    return (kotlin.math.round(this * 100) / 100).toString()
}

fun Float.round(size: Int = 2) = toDouble().round(size)