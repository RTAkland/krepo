/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 02:06
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.util

import cn.rtast.kmvnrepo.DEFAULT_CONFIG
import cn.rtast.kmvnrepo.entity.Config

class ConfigManager {
    private val file = rootPathOf("config.json").apply {
        if (!exists()) writeText(DEFAULT_CONFIG.toJson())
    }

    fun getConfig() = file.readText().fromJson<Config>()
}