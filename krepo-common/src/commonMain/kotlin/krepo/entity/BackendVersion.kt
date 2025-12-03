/*
 * Copyright © 2025 RTAkland
 * Date: 11/25/25, 12:25 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity

import kotlinx.serialization.Serializable

@Serializable
public data class BackendVersion(
    val version: Int,
    val patch: Int
)