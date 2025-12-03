/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 6:01 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.util.string

import nl.adaptivity.xmlutil.serialization.XML

inline fun <reified T : Any> String.fromXmlString(): T {
    return XML.decodeFromString<T>(this)
}

inline fun <reified T : Any> T.toXmlString(): String {
    return XML.encodeToString<T>(this)
}