/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:21 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity.user.oauth

import kotlinx.serialization.Serializable

@Serializable
public data class AzureUserInfo(
    val displayName: String,
    val id: String,
    val userPrincipalName: String
)