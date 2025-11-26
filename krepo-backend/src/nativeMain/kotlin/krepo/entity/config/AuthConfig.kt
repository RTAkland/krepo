/*
 * Copyright © 2025 RTAkland
 * Date: 11/2/25, 8:54 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity.config

import kotlinx.serialization.Serializable

@Serializable
data class AuthConfig(
    val azureClientID: String,
    val azureClientSecret: String,
    val redirectURI: String
)