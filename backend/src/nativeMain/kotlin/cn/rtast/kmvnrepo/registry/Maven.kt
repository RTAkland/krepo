/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 02:30
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.registry

import cn.rtast.kmvnrepo.configManager
import cn.rtast.kmvnrepo.enums.DeployStatus
import cn.rtast.kmvnrepo.repositories
import cn.rtast.kmvnrepo.util.exists
import cn.rtast.kmvnrepo.util.mkdirs
import cn.rtast.kmvnrepo.util.rootPathOf
import cn.rtast.kmvnrepo.util.writeByteArray
import kotlinx.io.files.Path

private val validatedArtifactExtension = listOf("klib", "jar", "aar")


fun deployMavenArtifact(path: String, bytes: ByteArray): DeployStatus {
    val splitPath = path.split("/")
    val filename = splitPath.last()
    val dir = splitPath.dropLast(1).joinToString("/")
    val targetDir = rootPathOf(dir).apply { mkdirs() }
    val targetFile = Path(targetDir, filename)
    val fileExtension = targetFile.toString().split(".").last()
    val repositoryConfig = repositories.find { it.name == splitPath.first() } ?: return DeployStatus.NotFound
    if (targetFile.exists() &&
        fileExtension in repositoryConfig.acceptExtensions &&
        !configManager.getConfig().allowRedeploy
    ) return DeployStatus.Conflict else {
        if (fileExtension in validatedArtifactExtension) {
            val version = splitPath.dropLast(1).last()
            if (version.endsWith("-SNAPSHOT") && !repositoryConfig.allowSnapshot) {
                return DeployStatus.NotAllowSNAPSHOT
            }
        }
        targetFile.writeByteArray(bytes)
        return DeployStatus.Success
    }
}
