/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:19 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util

import krepo.azure.cfg.ConfigManger
import krepo.azure.entity.res.FileEntry
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.net.URI


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
    val files = listFiles(path, null)
        .map { ObjectIdentifier.builder().key("${path.removeSuffix("/")}/${it.name}").build() }
    client.deleteObjects(
        DeleteObjectsRequest.builder()
            .bucket(ConfigManger.S3_BUCKET)
            .delete(
                Delete.builder()
                    .objects(files)
                    .build()
            ).build()
    )
}