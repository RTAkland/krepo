/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 02:30
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.registry

import cn.rtast.kmvnrepo.configManager
import cn.rtast.kmvnrepo.repositories
import cn.rtast.kmvnrepo.util.exists
import cn.rtast.kmvnrepo.util.mkdirs
import cn.rtast.kmvnrepo.util.rootPathOf
import cn.rtast.kmvnrepo.util.writeByteArray
import kotlinx.io.files.Path


fun deployMavenArtifact(path: String, bytes: ByteArray): Boolean {
    val splitPath = path.split("/")
    val filename = splitPath.last()
    val dir = splitPath.dropLast(1).joinToString("/")
    val targetDir = rootPathOf(dir).apply { mkdirs() }
    val targetFile = Path(targetDir, filename)
    if (targetFile.exists() && !configManager.getConfig().allowRedeploy) {
        return false
    } else {
        val extension = path.split(".").last()
        val repoName = path.split("/").first()
        val repo = repositories.find { it.name == repoName }!!
        if (extension !in repo.acceptExtensions) return false
        targetFile.writeByteArray(bytes)
        return true
    }
}
