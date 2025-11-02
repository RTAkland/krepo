/*
 * Copyright © 2025 RTAkland
 * Date: 11/2/25, 8:13 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class State(
    val state: String,
)