/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.util.string

import nl.adaptivity.xmlutil.serialization.XML

inline fun <reified T : Any> String.fromXmlString(): T {
    return XML.decodeFromString<T>(this)
}

inline fun <reified T : Any> T.toXmlString(): String {
    return XML.encodeToString<T>(this)
}