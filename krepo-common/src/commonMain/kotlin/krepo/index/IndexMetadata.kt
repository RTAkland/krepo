/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 6:28 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.index

import kotlinx.serialization.Serializable

/**
 * Protobuf
 */
@Serializable
public data class IndexMetadata(
    val repo: String,
    val group: String,
    val artifact: String,
    val version: String,
    val lastModified: Long
)