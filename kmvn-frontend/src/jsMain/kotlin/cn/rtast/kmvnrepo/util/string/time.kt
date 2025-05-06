/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/18 14:51
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util.string

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getDate(timestamp: Long): String {
    val dateTime = Instant.fromEpochSeconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedTime = "${dateTime.date} ${
        dateTime.time.hour.toString().padStart(2, '0')
    }:${dateTime.time.minute.toString().padStart(2, '0')}:${
        dateTime.time.second.toString().padStart(2, '0')
    }"

    return formattedTime
}