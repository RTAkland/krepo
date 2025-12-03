/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 7:20 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.util.repo

import krepo.backendVersion
import krepo.entity.maven.ConfigRepositoryWithSize
import krepo.entity.maven.GetRepositoriesResponse
import krepo.util.auth
import krepo.util.byte.fromProtoBuf
import krepo.util.file.LocalStorage
import krepo.util.httpRequest
import krepo.util.octetType

suspend fun getRepositories(): List<ConfigRepositoryWithSize> {
    val repositoriesAPIEndpoint =
        if (LocalStorage.TOKEN == null) backendVersion.LIST_PUBLIC_REPOSITORIES else backendVersion.LIST_ALL_REPOSITORIES
    val repositories = httpRequest(repositoriesAPIEndpoint)
        .auth().octetType().get().arrayBuffer().fromProtoBuf<GetRepositoriesResponse>()
        .data.sortedBy { it.visibility }
    return repositories
}