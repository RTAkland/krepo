/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 00:53
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.entity.res.NoEnoughParamResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


private suspend fun ApplicationCall.search(repository: String) {
    val groupQuery = request.queryParameters["group"]
    val artifactQuery = request.queryParameters["artifact"]
    if (groupQuery == null && artifactQuery == null) {
        return respond(HttpStatusCode.BadRequest, NoEnoughParamResponse(-200, "请提供`group` 或 `artifact` 进行搜索"))
    }
}

fun Application.configureAPIArtifactsRouting() {
//    publicRepositories.forEach {
//        route("/api/search/${it.name}") {
//            get("{path...}") { call.search(it.name) }
//        }
//    }
//
//    internalRepositories.forEach {
//        route("/api/search/${it.name}") {
//            authenticate("maven-common") {
//                get("{path...}") { call.search(it.name) }
//            }
//        }
//    }
}