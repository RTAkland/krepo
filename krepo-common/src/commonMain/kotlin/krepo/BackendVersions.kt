/*
 * Copyright © 2025 RTAkland
 * Date: 11/25/25, 12:31 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("ClassName", "PropertyName")

package krepo

public sealed class BackendVersions(
    public val v: Int,
    public val desc: RouteEndpoint,
) {
    public val BACKEND_VERSION: RouteEndpoint = createRouteEndpoint("api/version")
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
    public abstract val DELETE_DIRECTORY: RouteEndpoint
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


    public class LEGACY : BackendVersions(0, createRouteEndpoint("LEGACY")) {
        override val CURRENT_USER: RouteEndpoint = createRouteEndpoint("/@/api/user")
        override val USERS: RouteEndpoint = createRouteEndpoint("/@/api/user/")
        override val LOGIN: RouteEndpoint = createRouteEndpoint("/@/api/login")
        override val LOGOUT: RouteEndpoint = createRouteEndpoint("/@/api/logout")
        override val USER_INFO: RouteEndpoint = createRouteEndpoint("/@/api/user/{name}")
        override val CREATE_USER: RouteEndpoint = createRouteEndpoint("/@/api/user")
        override val DELETE_USER: RouteEndpoint = createRouteEndpoint("/@/api/user/")  // {username}
        override val MODIFY_USER: RouteEndpoint = createRouteEndpoint("/@/api/user/")  // {username}
        override val UPLOAD_FILE: RouteEndpoint = createRouteEndpoint("/@/api/repositories/upload")
        override val CREATE_DIRECTORY: RouteEndpoint = createRouteEndpoint("/@/api/repositories/create-directory")
        override val DELETE_DIRECTORY: RouteEndpoint = createRouteEndpoint("/@/api/repositories/delete")
        override val CREATE_REPOSITORY: RouteEndpoint = createRouteEndpoint("/@/api/repositories/new")
        override val MODIFY_REPOSITORY: RouteEndpoint = createRouteEndpoint("/@/api/repositories/modify")
        override val LIST_PUBLIC_REPOSITORIES: RouteEndpoint = createRouteEndpoint("/@/api/repositories/public")
        override val LIST_ALL_REPOSITORIES: RouteEndpoint = createRouteEndpoint("/@/api/repositories/all")
        override val DELETE_GAV: RouteEndpoint = createRouteEndpoint("/@/api/gav")
        override val DELETE_ARTIFACT: RouteEndpoint = createRouteEndpoint("/@/api/artifacts")
        override val GET_ARTIFACT_LATEST_VERSION: RouteEndpoint = createRouteEndpoint("/@/api/artifacts/versions/latest/")  // {repo.name} {path...}
        override val GET_PUBLIC_REPOSITORY_CONTENTS: RouteEndpoint = createRouteEndpoint("/@/api/contents/")  // {repo.name} {path...}
        override val GET_PRIVATE_REPOSITORY_CONTENTS: RouteEndpoint = NOT_IMPLEMENTED_API
        override val SEARCH_ARTIFACT: RouteEndpoint = createRouteEndpoint("/@/api/artifacts/search/")  // {repo.name}
        override val RESET_FRONTEND_CONFIG: RouteEndpoint = createRouteEndpoint("/@/api/config/frontend/reset")
        override val FRONTEND_CONFIG: RouteEndpoint = createRouteEndpoint("/@/api/config/frontend")
        override val MODIFY_FRONTEND_CONFIG: RouteEndpoint = createRouteEndpoint("/@/api/config/frontend")
        override val AZURE_SIGN_IN: RouteEndpoint = createRouteEndpoint("/api/signin/azure")
        override val AZURE_SIGN_IN_URL: RouteEndpoint = createRouteEndpoint("/api/signin/azure/url")
        override val AZURE_SIGN_IN_COMPLETE: RouteEndpoint = createRouteEndpoint("/api/signin/azure/complete")
    }

    public class V_25_11_25 : BackendVersions(1, createRouteEndpoint("Version at 2025-11-25")) {
        override val CURRENT_USER: RouteEndpoint = NOT_IMPLEMENTED_API
        override val USERS: RouteEndpoint = NOT_IMPLEMENTED_API
        override val LOGIN: RouteEndpoint = createRouteEndpoint("api/v2/login")
        override val LOGOUT: RouteEndpoint = createRouteEndpoint("api/v2/logout")
        override val USER_INFO: RouteEndpoint = NOT_IMPLEMENTED_API
        override val CREATE_USER: RouteEndpoint = NOT_IMPLEMENTED_API
        override val DELETE_USER: RouteEndpoint = NOT_IMPLEMENTED_API
        override val MODIFY_USER: RouteEndpoint = NOT_IMPLEMENTED_API
        override val UPLOAD_FILE: RouteEndpoint = createRouteEndpoint("api/v2/repository/operation/file")
        override val CREATE_DIRECTORY: RouteEndpoint = NOT_IMPLEMENTED_API
        override val DELETE_DIRECTORY: RouteEndpoint = createRouteEndpoint("api/v2/repository/operation/directory")
        override val CREATE_REPOSITORY: RouteEndpoint = NOT_IMPLEMENTED_API
        override val MODIFY_REPOSITORY: RouteEndpoint = NOT_IMPLEMENTED_API
        override val LIST_PUBLIC_REPOSITORIES: RouteEndpoint = createRouteEndpoint("api/v2/repository/public")
        override val LIST_ALL_REPOSITORIES: RouteEndpoint = createRouteEndpoint("api/v2/repository/all")
        override val DELETE_GAV: RouteEndpoint = NOT_IMPLEMENTED_API
        override val DELETE_ARTIFACT: RouteEndpoint = NOT_IMPLEMENTED_API
        override val GET_ARTIFACT_LATEST_VERSION: RouteEndpoint = NOT_IMPLEMENTED_API  // TODO
        override val GET_PUBLIC_REPOSITORY_CONTENTS: RouteEndpoint = createRouteEndpoint("api/v2/repository/public/contents")
        override val GET_PRIVATE_REPOSITORY_CONTENTS: RouteEndpoint = createRouteEndpoint("api/v2/repository/private/contents")
        override val SEARCH_ARTIFACT: RouteEndpoint = NOT_IMPLEMENTED_API
        override val RESET_FRONTEND_CONFIG: RouteEndpoint = NOT_IMPLEMENTED_API
        override val FRONTEND_CONFIG: RouteEndpoint = createRouteEndpoint("api/v2/config/frontend")
        override val MODIFY_FRONTEND_CONFIG: RouteEndpoint = NOT_IMPLEMENTED_API
        override val AZURE_SIGN_IN: RouteEndpoint = NOT_IMPLEMENTED_API
        override val AZURE_SIGN_IN_URL: RouteEndpoint = NOT_IMPLEMENTED_API
        override val AZURE_SIGN_IN_COMPLETE: RouteEndpoint = NOT_IMPLEMENTED_API
    }
}

public fun Int.toBackendVersion(): BackendVersions = when (this) {
    0 -> BackendVersions.LEGACY()
    1 -> BackendVersions.V_25_11_25()
    else -> throw IllegalArgumentException("Unknown backend version: $this")
}