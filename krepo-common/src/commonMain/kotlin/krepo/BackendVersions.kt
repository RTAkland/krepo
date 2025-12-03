/*
 * Copyright © 2025 RTAkland
 * Date: 11/25/25, 12:31 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("ClassName", "PropertyName")

package krepo

public sealed class BackendVersions(public val v: Int, public val desc: String) {
    public val V2_SEARCH_ARTIFACT: RouteEndpoint = createRouteEndpoint("/@/api/v2/artifacts/search")  // for LEGACY

    // user
    public abstract val CURRENT_USER: RouteEndpoint
    public abstract val USERS: RouteEndpoint
    public abstract val LOGIN: RouteEndpoint
    public abstract val LOGOUT: RouteEndpoint
    public abstract val USER_INFO: RouteEndpoint
    public abstract val CREATE_USER: RouteEndpoint
    public abstract val DELETE_USER: RouteEndpoint
    public abstract val MODIFY_USER: RouteEndpoint

    // repository
    public abstract val UPLOAD_FILE: RouteEndpoint
    public abstract val CREATE_DIRECTORY: RouteEndpoint
    public abstract val DELETE_REPOSITORY: RouteEndpoint
    public abstract val CREATE_REPOSITORY: RouteEndpoint
    public abstract val MODIFY_REPOSITORY: RouteEndpoint
    public abstract val LIST_PUBLIC_REPOSITORIES: RouteEndpoint
    public abstract val LIST_ALL_REPOSITORIES: RouteEndpoint

    // artifact
    public abstract val DELETE_GAV: RouteEndpoint
    public abstract val DELETE_ARTIFACT: RouteEndpoint
    public abstract val GET_ARTIFACT_LATEST_VERSION: RouteEndpoint
    public abstract val GET_PUBLIC_REPOSITORY_CONTENTS: RouteEndpoint
    public abstract val GET_PRIVATE_REPOSITORY_CONTENTS: RouteEndpoint
    public abstract val SEARCH_ARTIFACT: RouteEndpoint

    // config
    public abstract val RESET_FRONTEND_CONFIG: RouteEndpoint
    public abstract val FRONTEND_CONFIG: RouteEndpoint
    public abstract val MODIFY_FRONTEND_CONFIG: RouteEndpoint

    // azure oauth2
    public abstract val AZURE_SIGN_IN: RouteEndpoint
    public abstract val AZURE_SIGN_IN_URL: RouteEndpoint
    public abstract val AZURE_SIGN_IN_COMPLETE: RouteEndpoint

    // dangerous
    public abstract val CREATE_REPOSITORY_INDEX: RouteEndpoint
    public abstract val DELETE_REPOSITORY_INDEX: RouteEndpoint

    public abstract val PRIVACY: RouteEndpoint
    public abstract val TERMS: RouteEndpoint
    public abstract val LICENSES: RouteEndpoint

    public abstract val GRANT_PUBLISH_TOKEN: RouteEndpoint


    public class Legacy : BackendVersions(0, "Stable version") {
        override val CURRENT_USER: RouteEndpoint = createRouteEndpoint("/@/api/user")
        override val USERS: RouteEndpoint = createRouteEndpoint("/@/api/user/")
        override val LOGIN: RouteEndpoint = createRouteEndpoint("/@/api/login")
        override val LOGOUT: RouteEndpoint = createRouteEndpoint("/@/api/logout")
        override val USER_INFO: RouteEndpoint = createRouteEndpoint("/@/api/user/")  // {name}
        override val CREATE_USER: RouteEndpoint = createRouteEndpoint("/@/api/user")
        override val DELETE_USER: RouteEndpoint = createRouteEndpoint("/@/api/user/")  // {username}
        override val MODIFY_USER: RouteEndpoint = createRouteEndpoint("/@/api/user/")  // {username}
        override val UPLOAD_FILE: RouteEndpoint = createRouteEndpoint("/@/api/repositories/upload")
        override val CREATE_DIRECTORY: RouteEndpoint = createRouteEndpoint("/@/api/repositories/create-directory")
        override val DELETE_REPOSITORY: RouteEndpoint = createRouteEndpoint("/@/api/repositories/delete")
        override val CREATE_REPOSITORY: RouteEndpoint = createRouteEndpoint("/@/api/repositories/new")
        override val MODIFY_REPOSITORY: RouteEndpoint = createRouteEndpoint("/@/api/repositories/modify")
        override val LIST_PUBLIC_REPOSITORIES: RouteEndpoint = createRouteEndpoint("/@/api/repositories/public")
        override val LIST_ALL_REPOSITORIES: RouteEndpoint = createRouteEndpoint("/@/api/repositories/all")
        override val DELETE_GAV: RouteEndpoint = createRouteEndpoint("/@/api/gav")
        override val DELETE_ARTIFACT: RouteEndpoint = createRouteEndpoint("/@/api/artifacts")
        override val GET_ARTIFACT_LATEST_VERSION: RouteEndpoint =
            createRouteEndpoint("/@/api/artifacts/versions/latest/")  // {repo.name} {path...}
        override val GET_PUBLIC_REPOSITORY_CONTENTS: RouteEndpoint =
            createRouteEndpoint("/@/api/contents/")  // {repo.name} {path...}
        override val GET_PRIVATE_REPOSITORY_CONTENTS: RouteEndpoint = NOT_IMPLEMENTED_API
        override val SEARCH_ARTIFACT: RouteEndpoint = createRouteEndpoint("/@/api/artifacts/search/")  // {repo.name}
        override val RESET_FRONTEND_CONFIG: RouteEndpoint = createRouteEndpoint("/@/api/config/frontend/reset")
        override val FRONTEND_CONFIG: RouteEndpoint = createRouteEndpoint("/@/api/config/frontend")
        override val MODIFY_FRONTEND_CONFIG: RouteEndpoint = createRouteEndpoint("/@/api/config/frontend")
        override val AZURE_SIGN_IN: RouteEndpoint = createRouteEndpoint("/api/signin/azure")
        override val AZURE_SIGN_IN_URL: RouteEndpoint = createRouteEndpoint("/api/signin/azure/url")
        override val AZURE_SIGN_IN_COMPLETE: RouteEndpoint = createRouteEndpoint("/api/signin/azure/complete")
        override val CREATE_REPOSITORY_INDEX: RouteEndpoint = NOT_IMPLEMENTED_API
        override val DELETE_REPOSITORY_INDEX: RouteEndpoint = NOT_IMPLEMENTED_API
        override val PRIVACY: RouteEndpoint = NOT_IMPLEMENTED_API
        override val TERMS: RouteEndpoint = NOT_IMPLEMENTED_API
        override val LICENSES: RouteEndpoint = NOT_IMPLEMENTED_API
        override val GRANT_PUBLISH_TOKEN: RouteEndpoint = NOT_IMPLEMENTED_API
    }

    public class Azure : BackendVersions(1, "Running on Azure") {
        override val CURRENT_USER: RouteEndpoint = NOT_IMPLEMENTED_API
        override val USERS: RouteEndpoint = createRouteEndpoint("/api/azure/users")
        override val LOGIN: RouteEndpoint = createRouteEndpoint("/api/azure/login")
        override val LOGOUT: RouteEndpoint = createRouteEndpoint("/api/azure/logout")
        override val USER_INFO: RouteEndpoint = createRouteEndpoint("/api/azure/user/")
        override val CREATE_USER: RouteEndpoint = createRouteEndpoint("/api/azure/user")
        override val DELETE_USER: RouteEndpoint = NOT_IMPLEMENTED_API
        override val MODIFY_USER: RouteEndpoint = createRouteEndpoint("/api/azure/user/modify")
        override val UPLOAD_FILE: RouteEndpoint = createRouteEndpoint("/api/azure/repository/operation/file")
        override val CREATE_DIRECTORY: RouteEndpoint =
            createRouteEndpoint("/api/azure/repository/operation/directory/create")
        override val DELETE_REPOSITORY: RouteEndpoint = createRouteEndpoint("/api/azure/repository/delete")
        override val CREATE_REPOSITORY: RouteEndpoint = createRouteEndpoint("/api/azure/repository/create")
        override val MODIFY_REPOSITORY: RouteEndpoint = createRouteEndpoint("/api/azure/repository/modify")
        override val LIST_PUBLIC_REPOSITORIES: RouteEndpoint = createRouteEndpoint("/api/azure/repository/public")
        override val LIST_ALL_REPOSITORIES: RouteEndpoint = createRouteEndpoint("/api/azure/repository/all")
        override val DELETE_GAV: RouteEndpoint = createRouteEndpoint("/api/azure/repository/operation/gav")
        override val DELETE_ARTIFACT: RouteEndpoint = NOT_IMPLEMENTED_API
        override val GET_ARTIFACT_LATEST_VERSION: RouteEndpoint = NOT_IMPLEMENTED_API  // TODO
        override val GET_PUBLIC_REPOSITORY_CONTENTS: RouteEndpoint =
            createRouteEndpoint("/api/azure/repository/public/contents")
        override val GET_PRIVATE_REPOSITORY_CONTENTS: RouteEndpoint =
            createRouteEndpoint("/api/azure/repository/private/contents")
        override val SEARCH_ARTIFACT: RouteEndpoint = createRouteEndpoint("/api/azure/search")
        override val RESET_FRONTEND_CONFIG: RouteEndpoint = createRouteEndpoint("/api/azure/config/reset")
        override val FRONTEND_CONFIG: RouteEndpoint = createRouteEndpoint("/api/azure/config/frontend")
        override val MODIFY_FRONTEND_CONFIG: RouteEndpoint = createRouteEndpoint("/api/azure/config/modify")
        override val AZURE_SIGN_IN: RouteEndpoint = createRouteEndpoint("/api/azure/signin/azure")
        override val AZURE_SIGN_IN_URL: RouteEndpoint = createRouteEndpoint("/api/azure/signin/azure/url")
        override val AZURE_SIGN_IN_COMPLETE: RouteEndpoint = createRouteEndpoint("/api/azure/signin/azure/complete")
        override val CREATE_REPOSITORY_INDEX: RouteEndpoint = createRouteEndpoint("/api/azure/repository/index")
        override val DELETE_REPOSITORY_INDEX: RouteEndpoint = createRouteEndpoint("/api/azure/repository/index")
        override val PRIVACY: RouteEndpoint = createRouteEndpoint("/api/azure/system/privacy")
        override val TERMS: RouteEndpoint = createRouteEndpoint("/api/azure/system/terms")
        override val LICENSES: RouteEndpoint = createRouteEndpoint("/api/azure/system/licenses")
        override val GRANT_PUBLISH_TOKEN: RouteEndpoint = createRouteEndpoint("/api/azure/publish-credential")
    }
}

public fun Int.toBackendVersion(): BackendVersions = when (this) {
    0 -> BackendVersions.Legacy()
    1 -> BackendVersions.Azure()
    else -> throw IllegalArgumentException("Unknown backend version: $this")
}

public val BACKEND_VERSION_ROUTE: RouteEndpoint = createRouteEndpoint("/api/version")