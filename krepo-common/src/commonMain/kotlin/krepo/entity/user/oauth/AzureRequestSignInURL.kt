/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/3 20:09
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity.user.oauth

import kotlinx.serialization.Serializable

@Serializable
public data class AzureRequestSignInURL(
    val host: String,
    val v: String
)