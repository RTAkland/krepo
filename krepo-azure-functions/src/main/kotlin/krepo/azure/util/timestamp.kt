/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 6:04 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val String.timestamp: Long
    get() = LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        .toEpochSecond(ZoneOffset.UTC)
