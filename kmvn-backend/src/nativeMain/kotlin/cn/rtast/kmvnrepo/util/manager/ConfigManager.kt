/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/16 01:27
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.util.manager

import cn.rtast.kmvnrepo.DEFAULT_CONFIG
import cn.rtast.kmvnrepo.entity.config.Config
import cn.rtast.kmvnrepo.repositories
import cn.rtast.kmvnrepo.util.*

class ConfigManager {
    private val file = rootPathOf("config.json")

    init {
        if (!file.exists()) {
            file.writeText(DEFAULT_CONFIG.toJson())
        }
    }

    fun initRepositories() {
        getConfig().repositories.forEach {
            repositories.add(it.apply { rootPathOf(it.name).mkdirs() })
        }
    }

    fun getConfig() = file.readText().fromJson<Config>()

    fun write(data: Config) = file.writeText(data.toJson())
}