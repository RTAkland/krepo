/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 5:49 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.util

import cn.rtast.krepo.azure.cfg.ConfigManger
import cn.rtast.krepo.azure.entity.res.FileEntry
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

fun listFiles(path: String): List<FileEntry> {
    val prefix = if (path.isEmpty() || path.endsWith("/")) path else "$path/"
    val response = createClient().listObjectsV2(
        ListObjectsV2Request.builder()
            .bucket(ConfigManger.S3_BUCKET)
            .prefix(prefix)
            .delimiter("/")
            .build()
    )
    fun relativeName(fullKey: String): String {
        return if (fullKey.startsWith(prefix)) {
            fullKey.substring(prefix.length)
        } else fullKey
    }
    val folders = response.commonPrefixes()
        .map { cp ->
            val name = relativeName(cp.prefix())
            val dirName = if (name.endsWith("/")) name else "$name/"
            FileEntry(dirName.removeSuffix("/"), true, 0, 0)
        }
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