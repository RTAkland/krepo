/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 14:00
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.entity.RepositoryInfo
import cn.rtast.kmvnrepo.enums.RepositoryVisibility
import cn.rtast.kmvnrepo.util.mkdirs
import kotlinx.io.files.Path

const val ROOT_PATH_STRING = "./repositories"
val ROOT_PATH = Path(ROOT_PATH_STRING)

val repositories = mutableListOf<RepositoryInfo>(
    RepositoryInfo("releases", RepositoryVisibility.Public, Path(ROOT_PATH, "releases")),
    RepositoryInfo("snapshots", RepositoryVisibility.Public, Path(ROOT_PATH, "snapshots")),
    RepositoryInfo("private", RepositoryVisibility.Internal, Path(ROOT_PATH, "private"))
).apply { forEach { it.path.mkdirs() } }