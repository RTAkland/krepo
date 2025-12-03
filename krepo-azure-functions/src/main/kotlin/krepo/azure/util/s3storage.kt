/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:19 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.deleteObject
import aws.sdk.kotlin.services.s3.headObject
import aws.sdk.kotlin.services.s3.model.*
import aws.sdk.kotlin.services.s3.putObject
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.writeToOutputStream
import aws.smithy.kotlin.runtime.net.url.Url
import aws.smithy.kotlin.runtime.time.toJvmInstant
import kotlinx.coroutines.runBlocking
import krepo.azure.manager.ConfigManger
import krepo.azure.entity.internal.MetadataFile
import krepo.entity.maven.FileEntry
import java.io.ByteArrayOutputStream
import java.time.Instant


fun createClient(): S3Client {
    return S3Client {
        region = ConfigManger.S3_REGION
        endpointUrl = Url.parse(ConfigManger.S3API)
        forcePathStyle = true
        credentialsProvider = StaticCredentialsProvider {
            accessKeyId = ConfigManger.S3_ACCESS_KEY
            secretAccessKey = ConfigManger.S3_SECRET_KEY
        }
    }
}

fun headObject(filename: String) = runBlocking {
    return@runBlocking createClient().headObject {
        bucket = ConfigManger.S3_BUCKET
        key = filename
    }
}

fun exists(filename: String): Boolean = runBlocking {
    return@runBlocking try {
        createClient().headObject {
            bucket = ConfigManger.S3_BUCKET
            key = filename
        }
        true
    } catch (_: Exception) {
        false
    }
}

fun uploadFile(filename: String, file: ByteArray, overwrite: Boolean): Boolean = runBlocking {
    suspend fun upload() {
        createClient().putObject {
            bucket = ConfigManger.S3_BUCKET
            key = filename
            body = ByteStream.fromBytes(file)
        }
    }
    if (overwrite) {
        upload()
        return@runBlocking true
    } else {
        if (exists(filename)) {
            return@runBlocking false
        } else {
            upload()
            return@runBlocking true
        }
    }
}

fun getFileContentAsByteArray(filename: String): ByteArray? = runBlocking {
    return@runBlocking createClient().getObject(GetObjectRequest {
        bucket = ConfigManger.S3_BUCKET
        key = filename
    }) {
        val os = ByteArrayOutputStream()
        it.body?.writeToOutputStream(os) ?: return@getObject null
        os.toByteArray()
    }
}

fun getFileWithMetadata(
    filename: String,
    etag: String?,
    ifModifiedSince: Instant? = null,
): MetadataFile? = runBlocking {
    val client = createClient()
    suspend fun retrieveFile(fileKey: String): MetadataFile? {
        return try {
            val os = ByteArrayOutputStream()
            val resp = client.getObject(GetObjectRequest {
                bucket = ConfigManger.S3_BUCKET
                key = fileKey
            }) {
                it.body?.writeToOutputStream(os)
                it
            }
            val etag = resp.eTag!!
            val lastModified = resp.lastModified?.toJvmInstant()
            return MetadataFile(etag, os.toByteArray(), lastModified, false)
        } catch (_: NoSuchKey) {
            null
        }
    }
    if (etag == null && ifModifiedSince == null) return@runBlocking retrieveFile(filename) else {
        val resp = headObject(filename)
        val remoteEtag = resp.eTag!!
        val remoteLastModified = resp.lastModified!!.toJvmInstant()
        val matched = (etag != null && etag == remoteEtag) ||
                (ifModifiedSince != null && !remoteLastModified.isAfter(ifModifiedSince))
        return@runBlocking if (!matched) retrieveFile(filename)
        else MetadataFile(remoteEtag, null, remoteLastModified, true)
    }
}


fun listAllFiles(
    path: String,
    delimiter: String? = "/",
): List<String> = runBlocking {
    val client = createClient()
    val results = mutableListOf<String>()
    var continuationToken: String? = null

    do {
        val builder = ListObjectsV2Request {
            bucket = ConfigManger.S3_BUCKET
            prefix = path
            delimiter?.let { this.delimiter = it }
            continuationToken?.let { this.continuationToken = it }
        }
        val response = client.listObjectsV2(builder)
        fun relativeName(fullKey: String): String {
            return if (fullKey.startsWith(path)) {
                fullKey.substring(path.length)
            } else {
                fullKey
            }
        }

        val files = response.contents?.mapNotNull { obj ->
            if (obj.key != path) relativeName(obj.key!!) else null
        }
        files?.let { results.addAll(it) }
        continuationToken = response.nextContinuationToken
    } while (continuationToken != null)
    client.close()
    return@runBlocking results
}


fun listFiles(path: String, delimiter: String? = "/"): List<FileEntry> = runBlocking {
    val prefix = if (path.isEmpty() || path.endsWith("/")) path else "$path/"
    val builder = ListObjectsV2Request {
        bucket = ConfigManger.S3_BUCKET
        this.prefix = prefix
        delimiter?.let { this.delimiter = it }
    }
    val s3Client = createClient()
    val response = s3Client.listObjectsV2(builder)
    fun relativeName(fullKey: String): String {
        return if (fullKey.startsWith(prefix)) {
            fullKey.substring(prefix.length)
        } else fullKey
    }

    val folders = if (delimiter != null) {
        response.commonPrefixes?.map { cp ->
            val name = relativeName(cp.prefix ?: "")
            val dirName = if (name.endsWith("/")) name else "$name/"
            FileEntry(dirName.removeSuffix("/"), true, 0, 0)
        } ?: emptyList()
    } else emptyList()
    val files = response.contents?.filter { it.key != prefix }
        ?.map { obj ->
            val name = relativeName(obj.key ?: "")
            FileEntry(name.removeSuffix("/"), false, obj.size ?: 0L, obj.lastModified?.epochSeconds ?: 0L)
        } ?: emptyList()

    s3Client.close()
    return@runBlocking folders + files
}


fun deleteFile(path: String) = runBlocking {
    createClient().deleteObject {
        bucket = ConfigManger.S3_BUCKET
        key = path
    }
}

fun deleteDirectory(path: String) = runBlocking {
    val client = createClient()
    try {
        val normalizedPath = path.trimEnd('/')
        val files = listFiles(path, null)
            .map { file ->
                val key = if (normalizedPath.isEmpty()) file.name else "$normalizedPath/${file.name}"
                ObjectIdentifier { this.key = key }
            }.toList()
        if (files.isEmpty()) {
            println("No files found in $path to delete.")
            return@runBlocking
        }
        val deleteRequest = DeleteObjectsRequest {
            bucket = ConfigManger.S3_BUCKET
            delete = Delete { objects = files }
        }
        client.deleteObjects(deleteRequest)
    } catch (e: Exception) {
        println("Failed to delete objects: ${e.message}")
    } finally {
        client.close()
    }
}

fun deleteDirectoryIndividually(path: String) = runBlocking {
    val client = createClient()
    val normalizedPath = path.trimEnd('/')
    val files = listFiles(path, null)
    files.forEach { file ->
        val fileKey = if (normalizedPath.isEmpty()) file.name else "$normalizedPath/${file.name}"
        try {
            client.deleteObject {
                bucket = ConfigManger.S3_BUCKET
                key = fileKey
            }
        } catch (e: Exception) {
            println("Failed to delete $fileKey: ${e.message}")
        }
    }
    client.close()
}
