/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 5:50 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.cfg

object ConfigManger {
    private fun get(name: String): String = System.getenv(name)
//        .apply { println("Get env $name to $this") }
    val S3API = get("S3API")
    val S3_ACCESS_KEY = get("S3_ACCESS_KEY")
    val S3_SECRET_KEY = get("S3_SECRET_KEY")
    val S3_BUCKET = get("S3_BUCKET")
    val S3_REGION = get("S3_REGION")
    val CF_ACCOUNT_ID = get("CF_ACCOUNT_ID")
    val CF_EMAIL = get("CF_EMAIL")
    val CF_KV_TOKEN = get("CF_KV_TOKEN")
    val CF_KV_NAMESPACE_ID = get("CF_KV_NAMESPACE_ID")
}