/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/18 14:51
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.util.string

fun getDate(timestamp: Long): String {
    val date = js("new Date(timestamp * 1000)")
    fun pad(n: Int) = if (n < 10) "0$n" else "$n"
    val year = date.getFullYear() as Int
    val month = (date.getMonth() as Int) + 1
    val day = date.getDate() as Int
    val hour = date.getHours() as Int
    val minute = date.getMinutes() as Int
    val second = date.getSeconds() as Int

    return "$year-${pad(month)}-${pad(day)} ${pad(hour)}:${pad(minute)}:${pad(second)}"
}

fun getCurrentYear(): Int {
    val date = js("new Date()")
    return date.getFullYear() as Int
}
