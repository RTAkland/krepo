/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 14:00
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.entity.ConfigRepository
import cn.rtast.kmvnrepo.entity.FrontendConfig
import cn.rtast.kmvnrepo.entity.RepositoryVisibility
import cn.rtast.kmvnrepo.entity.config.Config
import cn.rtast.kmvnrepo.util.mkdirs
import kotlinx.io.files.Path

const val ROOT_PATH_STRING = "./repositories"
val ROOT_PATH = Path(ROOT_PATH_STRING).apply { mkdirs() }

val repositories = mutableListOf<ConfigRepository>()

private val defaultAcceptExtensions = listOf("jar", "klib", "aar")

val DEFAULT_CONFIG = Config(
    9098, false,
    listOf(
        ConfigRepository("releases", RepositoryVisibility.Public, defaultAcceptExtensions, false),
        ConfigRepository("snapshots", RepositoryVisibility.Public, defaultAcceptExtensions, true),
        ConfigRepository("private", RepositoryVisibility.Internal, defaultAcceptExtensions, true),
    ), true, "https://pkg.rtast.cn",
    FrontendConfig(
        "Maven Repo of RTAST",
        "皖ICP备2024066235-1号",
        "这里是RTAST的Maven仓库!",
        "© #YEAR# RTAkland|https://github.com/RTAkland|"
    )
)

val publicRepositories
    get() = repositories.filter { it.visibility == RepositoryVisibility.Public }
val internalRepositories
    get() = repositories.filter { it.visibility == RepositoryVisibility.Internal }
