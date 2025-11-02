/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 18:06
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity

import kotlinx.serialization.Serializable
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

typealias CreateRepository = ConfigRepository

@Serializable
data class ModifyRepository(
    val previousName: String,
    val newName: String,
    val visibility: RepositoryVisibility,
    val acceptExtensions: List<String>,
    val allowSnapshot: Boolean,
    var status: RepositoryStatus
)

@Serializable
data class ConfigRepositoryWithSize(
    val name: String,
    val visibility: RepositoryVisibility,
    val acceptExtensions: List<String>,
    val allowSnapshot: Boolean,
    var status: RepositoryStatus,
    val size: Long?,
)