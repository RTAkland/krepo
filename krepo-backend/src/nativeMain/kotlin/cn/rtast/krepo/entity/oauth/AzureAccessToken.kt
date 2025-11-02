/*
 * Copyright © 2025 RTAkland
 * Date: 11/2/25, 9:04 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.entity.oauth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AzureAccessToken(
    @SerialName("access_token")
    val accessToken: String
)