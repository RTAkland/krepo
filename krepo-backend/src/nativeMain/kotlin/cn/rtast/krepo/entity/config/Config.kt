/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.entity.config

import kotlinx.serialization.Serializable
import krepo.entity.ConfigRepository
import krepo.entity.FrontendConfig

@Serializable
data class Config(
    val port: Int,
    val allowRedeploy: Boolean,
    val repositories: List<ConfigRepository>,
    val allowFileListing: Boolean,
    val frontend: String,
    val frontendConfig: FrontendConfig,
    val auth: AuthConfig
)