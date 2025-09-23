/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:49
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo

import cn.rtast.krepo.entity.ConfigRepository
import cn.rtast.krepo.entity.FrontendConfig
import cn.rtast.krepo.entity.MirrorRepository
import cn.rtast.krepo.entity.RepositoryVisibility
import cn.rtast.krepo.enums.RepositoryStatus
import cn.rtast.krepo.entity.config.Config
import cn.rtast.krepo.util.file.mkdirs
import io.ktor.client.*
import io.ktor.client.engine.curl.Curl
import kotlinx.io.files.Path

val client = HttpClient(Curl)

const val REPOSITORY_PATH_STRING = "./repositories"
val REPOSITORY_PATH = Path(REPOSITORY_PATH_STRING).apply { mkdirs() }
const val DATA_PATH_STRING = "./data"
val DATA_PATH = Path(DATA_PATH_STRING).apply { mkdirs() }
val RESOURCE_PATH = Path(DATA_PATH, "static")

val repositories = mutableListOf<ConfigRepository>()

private val defaultAcceptExtensions = listOf(
    "jar", "klib", "aar",
    "so", "a", "dll",
    "dylib", "framework"
)

val DEFAULT_CONFIG = Config(
    9098, false,
    listOf(
        ConfigRepository(
            "releases",
            RepositoryVisibility.Public,
            defaultAcceptExtensions,
            false,
            RepositoryStatus.Available,
            listOf(MirrorRepository("https://example.com", MirrorRepository.Credential("user", "pass")))
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
    ), true, "https://pkg.rtast.cn",
    FrontendConfig(
        "Maven Repo of RTAST",
        "皖ICP备2024066235-1号",
        "这里是RTAST的Maven仓库!",
        "© #YEAR#  Made by  <a href=\"https://github.com/RTAkland\" target=\"_blank\">RTAkland</a>"
    )
)

val publicRepositories
    get() = repositories.filter { it.visibility == RepositoryVisibility.Public }
val internalRepositories
    get() = repositories.filter { it.visibility == RepositoryVisibility.Internal }
