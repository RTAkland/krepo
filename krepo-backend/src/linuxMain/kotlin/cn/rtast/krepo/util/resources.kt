/*
 * Copyright © 2025 RTAkland
 * Date: 9/24/25, 6:27 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.util

import cn.rtast.krepo.RESOURCE_PATH
import cn.rtast.krepo.fr.resources.getResource
import cn.rtast.krepo.fr.resources.resourceIndex
import cn.rtast.krepo.util.file.mkdirs
import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

fun initialResources() {
    RESOURCE_PATH.mkdirs()
    resourceIndex.forEach {
        Path(RESOURCE_PATH, it.key).parent?.mkdirs()
        val buffer = Buffer().apply { write(getResource(it.key).asByteArray()) }
        SystemFileSystem.sink(Path(RESOURCE_PATH, it.key)).buffered().use { use -> use.write(buffer, buffer.size) }
    }
    println("资源生成完成, 请重新启动krepo | Web static generated, please relaunch krepo.")
}