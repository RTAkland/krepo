/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 11:59
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("metadata")
data class MavenMetadata(
    @XmlElement(true)
    @XmlSerialName("groupId")
    val groupId: String,
    @XmlElement(true)
    @XmlSerialName("artifactId")
    val artifactId: String,
    @XmlElement(true)
    @XmlSerialName("versioning")
    val versioning: Versioning
) {
    @Serializable
    @XmlSerialName("versioning")
    data class Versioning(
        @XmlElement(true)
        @XmlSerialName("latest")
        val latest: String,
        @XmlElement(true)
        @XmlSerialName("release")
        val release: String,
        @XmlSerialName("versions")
        val versions: Versions,
        @XmlElement(true)
        @XmlSerialName("lastUpdated")
        val lastUpdated: String
    )

    @Serializable
    @XmlSerialName("versions")
    data class Versions(
        @XmlElement(true)
        @XmlSerialName("version")
        val version: List<String>
    )
}