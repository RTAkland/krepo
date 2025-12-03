/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 11:21 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalSerializationApi::class)

package krepo.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

public inline fun <reified T> ByteArray.fromProtobuf(): T {
    return ProtoBuf.decodeFromByteArray<T>(this)
}

public inline fun <reified T> T.toProtobuf(): ByteArray {
    return ProtoBuf.encodeToByteArray(this)
}