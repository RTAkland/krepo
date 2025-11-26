/*
 * Copyright © 2025 RTAkland
 * Date: 11/12/25, 9:16 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.entity.res

data class ConfigRepository(
    val name: String,
    val visibility: RepositoryVisibility,
    val acceptExtensions: List<String>,
    val allowSnapshot: Boolean,
    var status: RepositoryStatus,
    val mirrorRepositories: List<MirrorRepository>
)