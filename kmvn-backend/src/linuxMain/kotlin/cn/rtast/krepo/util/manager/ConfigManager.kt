/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:50
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.util.manager

import cn.rtast.kmvnrepo.enums.RepositoryStatus
import cn.rtast.krepo.DATA_PATH
import cn.rtast.krepo.DEFAULT_CONFIG
import cn.rtast.krepo.entity.config.Config
import cn.rtast.krepo.repositories
import cn.rtast.krepo.util.file.exists
import cn.rtast.krepo.util.file.mkdirs
import cn.rtast.krepo.util.file.readText
import cn.rtast.krepo.util.file.rootPathOf
import cn.rtast.krepo.util.file.writeText
import cn.rtast.krepo.util.string.fromJson
import cn.rtast.krepo.util.string.toJson
import kotlinx.io.files.Path

class ConfigManager {
    private val file = Path(DATA_PATH, "config.json")

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