/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 6:55 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.entity.res

import kotlinx.serialization.Serializable

@Serializable
data class CommonResponse<T>(
    val code: Int,
    val data: T
)