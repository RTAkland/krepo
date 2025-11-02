/*
 * Copyright © 2025 RTAkland
 * Date: 11/2/25, 10:04 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class AzureLoginSuccess(
    val t: String,
    val e: String,
    val n: String,
    val ex: Long
)