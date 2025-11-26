/*
 * Copyright © 2025 RTAkland
 * Date: 11/24/25, 10:48 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.entity.req

import com.google.gson.annotations.SerializedName

data class DeleteGAVReq(
    @SerializedName("gav")
    val path: String,
    val isDirectory: Boolean
)