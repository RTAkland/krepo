/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 02:07
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val port: Int,
    val allowRedeploy: Boolean
)