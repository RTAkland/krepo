/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 6:54 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.entity.req

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteGAVReq(
    @SerialName("gav")
    val path: String,
    val isDirectory: Boolean
)