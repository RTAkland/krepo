/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 14:00
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.entity.config.Config
import cn.rtast.kmvnrepo.entity.config.ConfigRepository
import cn.rtast.kmvnrepo.enums.RepositoryVisibility
import kotlinx.io.files.Path

const val ROOT_PATH_STRING = "./repositories"
val ROOT_PATH = Path(ROOT_PATH_STRING)

val repositories = mutableListOf<ConfigRepository>()

private val defaultAcceptExtensions = listOf("jar", "klib", "aar")

val DEFAULT_CONFIG = Config(
    9098, false,
    listOf(
        ConfigRepository("releases", RepositoryVisibility.Public, defaultAcceptExtensions),
        ConfigRepository("snapshots", RepositoryVisibility.Public, defaultAcceptExtensions),
        ConfigRepository("private", RepositoryVisibility.Internal, defaultAcceptExtensions),
    ), true, "https://mvnrepo.rtast.cn"
)

val publicRepositories
    get() = repositories.filter { it.visibility == RepositoryVisibility.Public }
val internalRepositories
    get() = repositories.filter { it.visibility == RepositoryVisibility.Internal }
