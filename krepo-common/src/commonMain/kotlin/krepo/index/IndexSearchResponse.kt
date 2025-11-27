/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 6:50 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.index

import kotlinx.serialization.Serializable

@Serializable
public data class IndexSearchResponse(
    val code: Int,
    val data: List<IndexMetadata>,
    val count: Int,
)