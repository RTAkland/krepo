/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:29 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.pages.users

import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import kotlinx.browser.window
import kotlinx.coroutines.launch
import krepo.backendVersion
import krepo.components.infoToast
import krepo.components.showDialog
import krepo.coroutineScope
import krepo.entity.GetUsersResponse
import krepo.util.auth
import krepo.util.checkImpl
import krepo.util.file.checkPermission
import krepo.util.fromJson
import krepo.util.httpRequest
import krepo.util.jsonContentType

fun RenderContext.userManagePage() {
    checkPermission {
        val showDeleteUserDialog = storeOf(false)
        val selectedUser = storeOf<GetUsersResponse.User?>(null)
        coroutineScope.launch {
            val users = httpRequest(backendVersion.USERS)
                .auth().acceptJson().jsonContentType()
                .get().checkImpl().body().fromJson<GetUsersResponse>().data
            div("container") {
                h2("title is-3 mt-4 mb-4") { +"Users" }
                div("columns is-multiline") {
                    users.forEach { user ->
                        div("column is-one-third") {
                            div("card") {
                                div("card-content") {
                                    p("title is-5") { +user.name }
                                    p("subtitle is-6 has-text-grey") { +user.email }
                                    div("buttons") {
                                        button("button is-small is-link") {
                                            i("fa-solid fa-user-pen mr-2") {}
                                            +"Edit"
                                            clicks handledBy {
                                                window.location.href = "/#/user/edit?username=${user.name}"
                                            }
                                        }
                                        button("button is-small is-danger") {
                                            i("fa-solid fa-trash mr-2") {}
                                            +"Delete"
                                            clicks handledBy {
                                                selectedUser.update(user)
                                                showDeleteUserDialog.update(true)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        showDialog(showDeleteUserDialog, "Delete User", "Do you want to delete the user?", {}) {
            coroutineScope.launch {
                httpRequest("${backendVersion.DELETE_USER}${selectedUser.current!!.name}")
                    .auth().acceptJson().jsonContentType()
                    .delete()
                infoToast("Deleted")
                window.location.reload()
            }
            infoToast("Deleting...")
        }
    }
}