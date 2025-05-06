/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 18:05
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity

import kotlinx.serialization.Serializable

@Serializable
enum class RepositoryVisibility(val desc: String) {
    Internal("私有仓库"), Public("公开仓库")
}