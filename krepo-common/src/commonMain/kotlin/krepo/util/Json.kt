/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:23 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalSerializationApi::class)

package krepo.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

public val json: Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    explicitNulls = false
    classDiscriminator = "_json_type_"
    encodeDefaults = true
    coerceInputValues = true
    decodeEnumsCaseInsensitive = true
    isLenient = true
}

public inline fun <reified T> T.toJson(): String {
    return json.encodeToString(this)
}

public inline fun <reified T> String.fromJson(): T {
    return json.decodeFromString<T>(this)
}