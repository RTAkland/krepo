/*
 * Copyright © 2025 RTAkland
 * Date: 9/26/25, 11:25 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class ResVersion(
    val properties: Map<String, String>,
) {
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(key: String): T {
        return properties[key]!! as T
    }

    override fun toString(): String {
        return properties.entries.joinToString("\n") { "${it.key}=${it.value}" }
    }

    operator fun compareTo(other: ResVersion): Int {
        return this.get<Int>("ver") - other.get<Int>("ver")
    }
}

fun Map<String, String>.toResVersion(): ResVersion = ResVersion(this)