/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 21:18
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val backend: String,
)