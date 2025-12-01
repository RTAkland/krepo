/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/13 22:41
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity.maven

import kotlinx.serialization.Serializable

@Serializable
public data class UploadFilePayload(
    val file: String,
    val filename: String,
    val path: String
)