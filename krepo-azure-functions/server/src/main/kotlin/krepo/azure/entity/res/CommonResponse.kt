/*
 * Copyright © 2025 RTAkland
 * Date: 11/12/25, 9:12 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.entity.res

data class CommonResponse<T>(
    val code: Int,
    val data: T
)