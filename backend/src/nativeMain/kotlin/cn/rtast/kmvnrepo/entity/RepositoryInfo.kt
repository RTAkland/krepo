/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:14
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.entity

import cn.rtast.kmvnrepo.enums.RepositoryVisibility
import kotlinx.io.files.Path

data class RepositoryInfo(
    val name: String,
    val visibility: RepositoryVisibility,
    val path: Path
)