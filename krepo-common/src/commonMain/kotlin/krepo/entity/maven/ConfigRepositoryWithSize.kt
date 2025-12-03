/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/3 21:37
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity.maven

import kotlinx.serialization.Serializable
import krepo.enums.RepositoryStatus

@Serializable
public data class ConfigRepositoryWithSize(
    val name: String,
    val visibility: RepositoryVisibility,
    var status: RepositoryStatus,
    val size: Long?,
)