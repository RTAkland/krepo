/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:38 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.util.manager

import krepo.DATA_PATH
import krepo.DEFAULT_CONFIG
import krepo.entity.config.Config
import krepo.repositories
import krepo.util.file.exists
import krepo.util.file.mkdirs
import krepo.util.file.readText
import krepo.util.file.rootPathOf
import krepo.util.file.writeText
import kotlinx.io.files.Path
import krepo.enums.RepositoryStatus
import krepo.util.fromJson
import krepo.util.toJson

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