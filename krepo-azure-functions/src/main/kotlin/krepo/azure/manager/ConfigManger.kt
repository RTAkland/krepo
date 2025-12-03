/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 5:42 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.manager

object ConfigManger {
    private fun get(name: String): String = System.getenv(name)
    private fun getOptional(name: String): String? = System.getenv(name)

    val S3API = get("S3API")
    val S3_ACCESS_KEY = get("S3_ACCESS_KEY")
    val S3_SECRET_KEY = get("S3_SECRET_KEY")
    val S3_BUCKET = get("S3_BUCKET")
    val S3_REGION = get("S3_REGION")

    val AZURE_CLIENT_ID = get("AZURE_CLIENT_ID")
    val AZURE_CLIENT_SECRET = get("AZURE_CLIENT_SECRET")
    val AZURE_REDIRECT_URL = get("AZURE_REDIRECT_URL")

    val AUTH_STORAGE_TYPE = get("AUTH_STORAGE_TYPE")  // CF_KV or REDIS

    val CF_ACCOUNT_ID = getOptional("CF_ACCOUNT_ID")
    val CF_EMAIL = getOptional("CF_EMAIL")
    val CF_KV_TOKEN = getOptional("CF_KV_TOKEN")
    val CF_KV_NAMESPACE_ID = getOptional("CF_KV_NAMESPACE_ID")

    val REDIS_URI = getOptional("REDIS_URI")

    val FRONTEND_URL = get("FRONTEND_URL")

    val JWT_SECRET = get("JWT_SECRET")
}