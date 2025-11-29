/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 1:49 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity.user

import kotlinx.serialization.Serializable

@Serializable
public data class NoSensitiveUser(
    val name: String,
    val email: String
)