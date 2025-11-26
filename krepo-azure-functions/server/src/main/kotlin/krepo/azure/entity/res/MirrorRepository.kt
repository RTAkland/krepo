/*
 * Copyright © 2025 RTAkland
 * Date: 11/12/25, 9:17 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.entity.res

import kotlinx.serialization.Serializable

data class MirrorRepository(
    val url: String,
    val credential: Credential?,
) {
    @Serializable
    data class Credential(
        val username: String,
        val password: String
    )
}