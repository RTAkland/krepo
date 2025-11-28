/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.util

fun Double.round(size: Int = 2): String {
    return (kotlin.math.round(this * 100) / 100).toString()
}

fun Float.round(size: Int = 2) = toDouble().round(size)