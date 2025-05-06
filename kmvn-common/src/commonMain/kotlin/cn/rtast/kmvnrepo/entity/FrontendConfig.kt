/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 14:20
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class FrontendConfig(
    val pageTitle: String,
    val icpLicense: String?,
    val description: String?,
    val copyright: String
)