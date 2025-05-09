/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 14:00
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.entity.ConfigRepository
import cn.rtast.kmvnrepo.entity.FrontendConfig
import cn.rtast.kmvnrepo.entity.ProxiedRepository
import cn.rtast.kmvnrepo.entity.RepositoryVisibility
import cn.rtast.kmvnrepo.entity.config.Config
import cn.rtast.kmvnrepo.enums.RepositoryStatus
import cn.rtast.kmvnrepo.util.http.clientEngine
import cn.rtast.kmvnrepo.util.string.mkdirs
import io.ktor.client.*
import kotlinx.io.files.Path

val client = HttpClient(clientEngine)
const val ROOT_PATH_STRING = "./repositories"
val ROOT_PATH = Path(ROOT_PATH_STRING).apply { mkdirs() }

var repositories = mutableListOf<ConfigRepository>()

private val defaultAcceptExtensions = listOf("jar", "klib", "aar")

val DEFAULT_CONFIG = Config(
    9098, false,
    listOf(
        ConfigRepository(
            "releases",
            RepositoryVisibility.Public,
            defaultAcceptExtensions,
            false,
            RepositoryStatus.Available,
            listOf(
                ProxiedRepository(
                    "https://repo.maven.rtast.cn/releases",
                    ProxiedRepository.Credential(
                        "username", "password"
                    ),
                )
            )
        ),
        ConfigRepository(
            "snapshots",
            RepositoryVisibility.Public,
            defaultAcceptExtensions,
            true,
            RepositoryStatus.Available,
            listOf()
        ),
        ConfigRepository(
            "private",
            RepositoryVisibility.Internal,
            defaultAcceptExtensions,
            true,
            RepositoryStatus.Available,
            listOf()
        ),
    ),
    true, "https://pkg.rtast.cn",
    FrontendConfig(
        "Maven Repo of RTAST",
        "皖ICP备2024066235-1号",
        "这里是RTAST的Maven仓库!",
        "© #YEAR#  Made by  <a href=\"https://github.com/RTAkland\" target=\"_blank\">RTAkland</a>"
    ),
)

val publicRepositories
    get() = repositories.filter { it.visibility == RepositoryVisibility.Public }
val internalRepositories
    get() = repositories.filter { it.visibility == RepositoryVisibility.Internal }
