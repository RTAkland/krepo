/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:40 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity.res

import kotlinx.serialization.Serializable
import krepo.entity.FileEntry

@Serializable
data class APIListingResponse(
    val code: Int,
    val count: Int,
    val data: List<FileEntry>
)