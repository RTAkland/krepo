/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:19 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util

import krepo.azure.cfg.ConfigManger
import krepo.azure.entity.internal.MetadataFile
import krepo.entity.maven.FileEntry
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.net.URI
import java.time.Instant


fun createClient(): S3Client {
    return S3Client.builder()
        .region(Region.of(ConfigManger.S3_REGION))
        .endpointOverride(URI.create(ConfigManger.S3API))
        .forcePathStyle(true)
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    ConfigManger.S3_ACCESS_KEY,
                    ConfigManger.S3_SECRET_KEY
                )
            )
        ).build()
}

fun exists(filename: String): Boolean {
    return try {
        createClient().headObject(
            HeadObjectRequest.builder()
                .bucket(ConfigManger.S3_BUCKET)
                .key(filename)
                .build()
        )
        true
    } catch (_: NoSuchKeyException) {
        false
    }
}

fun uploadFile(filename: String, file: ByteArray, overwrite: Boolean): Boolean {
    fun upload() {
        createClient().putObject(
            PutObjectRequest.builder()
                .bucket(ConfigManger.S3_BUCKET)
                .key(filename)
                .build(),
            RequestBody.fromBytes(file)
        )
    }
    if (overwrite) {
        upload()
        return true
    } else {
        if (exists(filename)) {
            return false
        } else {
            upload()
            return true
        }
    }
}

fun getFile(filename: String): ByteArray? {
    if (!exists(filename)) return null
    return createClient().getObject(
        GetObjectRequest.builder()
            .bucket(ConfigManger.S3_BUCKET)
            .key(filename)
            .build()
    ).readBytes()
}

fun getFileWithMetadata(
    filename: String,
    etag: String?,
    ifModifiedSince: Instant? = null,
): MetadataFile? {
    val client = createClient()
    fun retrieveFile(key: String): MetadataFile? {
        if (!exists(key)) return null
        val resp = client.getObject(
            GetObjectRequest.builder()
                .bucket(ConfigManger.S3_BUCKET)
                .key(key)
                .build()
        )
        val bytes = resp.readBytes()
        val etag = resp.response().eTag()
        val lastModified = resp.response().lastModified()
        return MetadataFile(etag, bytes, lastModified, false)
    }
    if (etag == null && ifModifiedSince == null) return retrieveFile(filename) else {
        val resp = client.headObject(
            HeadObjectRequest.builder()
                .bucket(ConfigManger.S3_BUCKET)
                .key(filename)
                .build()
        )
        val remoteEtag = resp.eTag()
        val remoteLastModified = resp.lastModified()
        val matched = (etag != null && etag == remoteEtag) ||
                (ifModifiedSince != null && !remoteLastModified.isAfter(ifModifiedSince))
        return if (!matched) retrieveFile(filename)
        else MetadataFile(remoteEtag, null, remoteLastModified, true)
    }
}


fun listAllFiles(
    path: String,
    delimiter: String? = "/",
): List<String> {
    val client = createClient()
    val results = mutableListOf<String>()
    var continuationToken: String? = null
    do {
        val builder = ListObjectsV2Request.builder()
            .bucket(ConfigManger.S3_BUCKET)
            .prefix(path)
        if (delimiter != null) builder.delimiter(delimiter)
        if (continuationToken != null) builder.continuationToken(continuationToken)
        val response = client.listObjectsV2(builder.build())
        fun relativeName(fullKey: String): String {
            return if (fullKey.startsWith(path)) {
                fullKey.substring(path.length)
            } else fullKey
        }

        val files = response.contents()
            .filter { it.key() != path }
            .map { obj -> relativeName(obj.key()) }
        results.addAll(files)
        continuationToken = if (response.isTruncated) response.nextContinuationToken() else null
    } while (continuationToken != null)
    return results
}


fun listFiles(path: String, delimiter: String? = "/"): List<FileEntry> {
    val prefix = if (path.isEmpty() || path.endsWith("/")) path else "$path/"
    val builder = ListObjectsV2Request.builder()
        .bucket(ConfigManger.S3_BUCKET)
        .prefix(prefix)

    if (delimiter != null) {
        builder.delimiter(delimiter)
    }

    val response = createClient().listObjectsV2(builder.build())

    fun relativeName(fullKey: String): String {
        return if (fullKey.startsWith(prefix)) {
            fullKey.substring(prefix.length)
        } else fullKey
    }

    val folders = if (delimiter != null) {
        response.commonPrefixes()
            .map { cp ->
                val name = relativeName(cp.prefix())
                val dirName = if (name.endsWith("/")) name else "$name/"
                FileEntry(dirName.removeSuffix("/"), true, 0, 0)
            }
    } else emptyList()

    val files = response.contents()
        .filter { it.key() != prefix }
        .map { obj ->
            val name = relativeName(obj.key())
            FileEntry(name.removeSuffix("/"), false, obj.size(), obj.lastModified().epochSecond)
        }

    return folders + files
}


fun deleteFile(path: String) {
    createClient().deleteObject(
        DeleteObjectRequest.builder()
            .bucket(ConfigManger.S3_BUCKET)
            .key(path)
            .build()
    )
}

fun deleteDirectory(path: String) {
    val client = createClient()
    try {
        val normalizedPath = path.trimEnd('/')
        val files = listFiles(path, null)
            .map { file ->
                val key = if (normalizedPath.isEmpty()) file.name else "$normalizedPath/${file.name}"
                ObjectIdentifier.builder().key(key).build()
            }
            .toList()
        if (files.isEmpty()) {
            println("No files found in $path to delete.")
            return
        }
        val deleteRequest = DeleteObjectsRequest.builder()
            .bucket(ConfigManger.S3_BUCKET)
            .delete(Delete.builder().objects(files).build())
            .build()

        client.deleteObjects(deleteRequest)
    } finally {
        client.close()
    }
}

fun deleteDirectoryIndividually(path: String) {
    val client = createClient()
    val normalizedPath = path.trimEnd('/')
    val files = listFiles(path, null)
    files.forEach { file ->
        val key = if (normalizedPath.isEmpty()) file.name else "$normalizedPath/${file.name}"
        try {
            client.deleteObject { it.bucket(ConfigManger.S3_BUCKET).key(key) }
        } catch (e: Exception) {
            println("Failed to delete $key: ${e.message}")
        }
    }
    client.close()
}
