/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 14:00
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.entity.Config
import cn.rtast.kmvnrepo.entity.RepositoryInfo
import cn.rtast.kmvnrepo.enums.RepositoryVisibility
import cn.rtast.kmvnrepo.util.mkdirs
import cn.rtast.kmvnrepo.util.rootPathOf
import kotlinx.io.files.Path

const val ROOT_PATH_STRING = "./repositories"
val ROOT_PATH = Path(ROOT_PATH_STRING)

val repositories = mutableListOf<RepositoryInfo>(
    RepositoryInfo("releases", RepositoryVisibility.Public),
    RepositoryInfo("snapshots", RepositoryVisibility.Public),
    RepositoryInfo("private", RepositoryVisibility.Internal)
).apply { forEach { rootPathOf(it.name).mkdirs() } }

val publicRepositories = repositories.filter { it.visibility == RepositoryVisibility.Public }
val internalRepositories = repositories.filter { it.visibility == RepositoryVisibility.Internal }

val DEFAULT_CONFIG = Config(
    9098, false
)