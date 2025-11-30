/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 6:55 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.entity.res

import kotlinx.serialization.Serializable
import krepo.entity.RepositoryVisibility
import krepo.enums.RepositoryStatus

@Serializable
data class ConfigRepository(
    val name: String,
    val visibility: RepositoryVisibility,
    val acceptExtensions: List<String>,
    val allowSnapshot: Boolean,
    var status: RepositoryStatus,
    val mirrorRepositories: List<MirrorRepository>
)