/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 20:11
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.enums

import kotlinx.serialization.Serializable

@Serializable
enum class RepositoryVisibility(val desc: String) {
    Public("公开仓库"),
    Internal("私有仓库")
}