/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 11:12 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util.hex

import java.security.MessageDigest

val String.md5: String
    get() {
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(this.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }