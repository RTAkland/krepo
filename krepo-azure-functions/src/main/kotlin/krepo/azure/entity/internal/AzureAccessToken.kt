/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 5:59 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.azure.entity.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AzureAccessToken(
    @SerialName("access_token")
    val accessToken: String,
)