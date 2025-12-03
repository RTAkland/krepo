/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/3 21:15
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity

import kotlinx.serialization.Serializable

@Serializable
public data class CommonResponse<T>(
    val code: Int,
    val data: T
)