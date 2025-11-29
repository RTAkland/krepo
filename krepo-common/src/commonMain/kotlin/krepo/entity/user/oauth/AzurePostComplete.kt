/*
 * Copyright © 2025 RTAkland
 * Date: 11/28/25, 12:15 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity.user.oauth

import kotlinx.serialization.Serializable

@Serializable
public data class AzurePostComplete(
    val state: String
)