/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 00:53
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.util.string

fun parseGAV(path: String, repo: String): Triple<String, String, String> {
    val parts = path.removePrefix("/$repo/").trim('/').split("/")
    val version = parts.last()
    val artifactId = parts[parts.size - 2]
    val groupParts = parts.subList(0, parts.size - 2)
    val groupId = groupParts.joinToString(".")
    return Triple(groupId, artifactId, version)
}