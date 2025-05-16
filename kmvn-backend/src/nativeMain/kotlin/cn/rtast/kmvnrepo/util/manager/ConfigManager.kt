/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/16 01:27
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.util.manager

import cn.rtast.kmvnrepo.DATA_PATH
import cn.rtast.kmvnrepo.DEFAULT_CONFIG
import cn.rtast.kmvnrepo.entity.config.Config
import cn.rtast.kmvnrepo.enums.RepositoryStatus
import cn.rtast.kmvnrepo.repositories
import cn.rtast.kmvnrepo.util.file.exists
import cn.rtast.kmvnrepo.util.file.mkdirs
import cn.rtast.kmvnrepo.util.file.readText
import cn.rtast.kmvnrepo.util.file.rootPathOf
import cn.rtast.kmvnrepo.util.file.writeText
import cn.rtast.kmvnrepo.util.string.fromJson
import cn.rtast.kmvnrepo.util.string.toJson
import kotlinx.io.files.Path

class ConfigManager {
    private val file = Path(DATA_PATH,"config.json")

    init {
        if (!file.exists()) {
            file.writeText(DEFAULT_CONFIG.toJson())
        }
        val config = this.getConfig()
        config.repositories.forEach {
            it.status = RepositoryStatus.Available
        }
        this.write(config)
    }

    fun initRepositories() {
        getConfig().repositories.forEach {
            repositories.add(it.apply { rootPathOf(it.name).mkdirs() })
        }
    }

    fun getConfig() = file.readText().fromJson<Config>()

    fun write(data: Config) = file.writeText(data.toJson())
}