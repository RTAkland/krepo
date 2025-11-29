/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 5:31 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util.repo.index

import krepo.azure.util.deleteFile
import krepo.azure.util.getFile
import krepo.azure.util.listAllFiles
import krepo.azure.util.string.fromXmlString
import krepo.azure.util.timestamp
import krepo.azure.util.uploadFile
import krepo.index.IndexMetadata

object Indexer {
    private fun String.parseIndexMetadata(): IndexMetadata {
        val parts = this.split("@")
        return IndexMetadata(parts[1], parts[2], parts.first(), parts[3], parts.last().toLong())
    }

    /**
     * `This API is dangerous, please use it with caution.`
     *
     * `This API is dangerous, please use it with caution.`
     *
     * `This API is dangerous, please use it with caution.`
     *
     * `This API is dangerous, please use it with caution.`
     *
     * `This API is dangerous, please use it with caution.`
     *
     * `This API is dangerous, please use it with caution.`
     *
     * `This API is dangerous, please use it with caution.`
     */
    fun indexAll(repository: String) {
        val files = listAllFiles(repository, null)
            .filter { it.endsWith("maven-metadata.xml") }
        files.forEach {
            val content = getFile("${repository}$it")!!
            indexOnce(repository, it, content)
        }
    }

    fun indexOnce(repository: String, path: String, originContent: ByteArray) {
        val parts = path.split("/")
        val artifactId = parts.dropLast(1).last()
        val group = parts.dropLast(2).joinToString(".")
        val metadata = originContent.toString(Charsets.UTF_8).fromXmlString<MavenMetadata>()
        val latestVersion = metadata.versioning.latest
        val timestamp = metadata.versioning.lastUpdated.timestamp
        val indexFileName = "artifacts-index/${artifactId}@${repository}@${group}@${latestVersion}@${timestamp}"
        uploadFile(indexFileName, byteArrayOf(0x01), true)
        println("Indexing $indexFileName")
    }

    fun deleteIndex(repository: String) {
        listAllFiles("artifacts-index/")
            .filter { it.parseIndexMetadata().repo == repository }
            .forEach { deleteFile("artifacts-index/$it") }
    }

    fun search(keyword: String, repo: String? = null): List<IndexMetadata> {
        val files = listAllFiles("artifacts-index/")
            .filter { it.contains(keyword) }
        val result = if (repo != null) files.filter { it.split("@")[1] == repo } else files
        return result.map { it.parseIndexMetadata() }
    }
}