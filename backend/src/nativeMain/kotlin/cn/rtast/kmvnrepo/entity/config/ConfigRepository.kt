/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 21:00
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity.config

import cn.rtast.kmvnrepo.enums.RepositoryVisibility
import kotlinx.serialization.Serializable

@Serializable
data class ConfigRepository(
    val name: String,
    val visibility: RepositoryVisibility,
    val acceptExtensions: List<String>,
)