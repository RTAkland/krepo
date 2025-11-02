/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/10 19:57
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class CreateDirectoryRequest(
    val path: String
)

@Serializable
data class CreateDirectoryResponse(
    val code: Int
)