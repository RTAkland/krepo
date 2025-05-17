/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 15:42
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.util.file

fun formatSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = listOf("KB", "MB", "GB", "TB", "PB", "EB")
    var size = bytes.toDouble() / 1024
    var unitIndex = 0
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    return "${roundToTwoDecimals(size)} ${units[unitIndex]}"
}

fun roundToTwoDecimals(value: Double): String {
    val scaled = (value * 100).toInt()
    val remainder = (value * 1000).toInt() % 10
    val rounded = if (remainder >= 5) scaled + 1 else scaled
    val result = rounded / 100.0
    return result.toString()
}
