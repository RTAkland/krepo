/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 21:01
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.entity.config

import cn.rtast.kmvnrepo.entity.ConfigRepository
import cn.rtast.kmvnrepo.entity.FrontendConfig
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val port: Int,
    val allowRedeploy: Boolean,
    val repositories: List<ConfigRepository>,
    val allowFileListing: Boolean,
    val frontend: String,
    val frontendConfig: FrontendConfig,
)