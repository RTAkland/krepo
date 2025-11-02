/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 18:13
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class DeleteRepository(
    val name: String
)