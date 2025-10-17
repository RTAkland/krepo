/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("unused")

package cn.rtast.krepo.util.string

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json


val json: Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    classDiscriminator = "_json_type_"
    encodeDefaults = true
    coerceInputValues = true
    decodeEnumsCaseInsensitive = true
    isLenient = true
    encodeDefaults = true
    explicitNulls = true
}

inline fun <reified T> T.toJson(): String {
    return json.encodeToString(this)
}

inline fun <reified T> String.fromJson(): T {
    return json.decodeFromString<T>(this)
}