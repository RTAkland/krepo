/*
 * Copyright © 2025 RTAkland
 * Date: 11/2/25, 9:12 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.routing.api.oauth

import kotlinx.serialization.Serializable

@Serializable
data class AzureUserInfo(
    val displayName: String,
    val id: String,
    val userPrincipalName: String
)