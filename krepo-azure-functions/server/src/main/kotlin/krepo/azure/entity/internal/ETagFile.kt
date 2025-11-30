/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/30 20:33
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.entity.internal

data class ETagFile(
    val etag: String,
    val bytes: ByteArray?,
    val matched: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ETagFile

        if (etag != other.etag) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = etag.hashCode()
        result = 31 * result + (bytes?.contentHashCode() ?: 0)
        return result
    }
}