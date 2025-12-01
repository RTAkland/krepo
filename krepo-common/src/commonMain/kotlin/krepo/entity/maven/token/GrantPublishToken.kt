/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/1 21:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity.maven.token

import kotlinx.serialization.Serializable

@Serializable
public data class GrantPublishTokenRequest(
    val name: String
)

@Serializable
public data class GrantPublishTokenResponse(
    val name: String,
    val token: String,
    val expire: Long
)