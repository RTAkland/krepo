/*
 * Copyright © 2025 RTAkland
 * Date: 9/24/25, 6:27 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.util

import cn.rtast.krepo.RESOURCE_PATH
import cn.rtast.krepo.entity.ResVersion
import cn.rtast.krepo.entity.toResVersion
import cn.rtast.krepo.fr.resources.getResource
import cn.rtast.krepo.fr.resources.resourceIndex
import cn.rtast.krepo.util.file.*
import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.system.exitProcess

fun initialResources(isUpdateRes: Boolean = false) {
    if (isUpdateRes) {
        RESOURCE_PATH.deleteRec()
    }
    RESOURCE_PATH.mkdirs()
    resourceIndex.forEach {
        println(it.key)
        Path(RESOURCE_PATH, it.key).parent?.mkdirs()
        val buffer = Buffer().apply { write(getResource(it.key).asByteArray()) }
        SystemFileSystem.sink(Path(RESOURCE_PATH, it.key)).buffered().use { use -> use.write(buffer, buffer.size) }
    }
    if (isUpdateRes) {
        println("资源升级成功, 请重新启动krepo | Web static updated, please relaunch krepo.")
    } else {
        println("资源生成完成, 请重新启动krepo | Web static generated, please relaunch krepo.")
    }
}

fun ResVersion.syncToDisk() {
    Path(RESOURCE_PATH, "res_version.txt").writeText(this.toString())
}

fun String.parseResVersionTxt(): ResVersion {
    val diskVer = this.split("\n").associate {
        val kv = it.split("=")
        kv.first() to kv.last()
    }.toResVersion()
    return diskVer
}

fun compareResVersion() {
    val diskVerPath = Path(RESOURCE_PATH, "res_version.txt")
    val diskVer = (if (diskVerPath.exists()) diskVerPath.readText()
    else getResource("res_version.txt").asString()).parseResVersionTxt().apply { syncToDisk() }
    val internalVer = getResource("res_version.txt")
        .asString().parseResVersionTxt()
    if (internalVer > diskVer) {
        internalVer.syncToDisk()
        initialResources(true)
    }
}