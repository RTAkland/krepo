/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/15 20:08
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.util.string

fun extractQueryParams(url: String): Map<String, String> {
    val queryPart = url.substringAfter("?", "")
    if (queryPart.isEmpty()) return emptyMap()

    return queryPart.split("&").associate {
        val (key, value) = it.split("=", limit = 2).let { parts ->
            if (parts.size == 2) parts[0] to parts[1] else null to null
        }
        key!! to value!!
    }
}