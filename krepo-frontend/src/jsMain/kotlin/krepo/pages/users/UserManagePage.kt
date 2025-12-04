/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:29 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.pages.users

import dev.fritz2.core.RenderContext
import dev.fritz2.core.disabled
import dev.fritz2.core.storeOf
import kotlinx.browser.window
import krepo.backendVersion
import krepo.util.img.autoFASvg
import krepo.components.infoToast
import krepo.components.showDialog
import krepo.coroutineScope
import krepo.entity.user.GetUsersResponse
import krepo.entity.user.NoSensitiveUser
import krepo.util.*
import krepo.util.byte.fromProtoBuf
import krepo.util.file.checkPermission

fun RenderContext.UserManagePage() {
    checkPermission {
        val showDeleteUserDialog = storeOf(false)
        val selectedUser = storeOf<NoSensitiveUser?>(null)
        coroutineScope.launchJob {
            val users = httpRequest(backendVersion.USERS)
                .auth().get().arrayBuffer().fromProtoBuf<GetUsersResponse>().data
            div("container") {
                h2("title is-3 mt-4 mb-4") { +"Users" }
                div("columns is-multiline") {
                    users.forEach { user ->
                        div("column is-one-third") {
                            div("card") {
                                div("card-content") {
                                    p("title is-5") { +user.name }
                                    p("subtitle is-6 has-text-grey") { +user.email }
                                    span("has-text-grey") { +user.ex!!.namespaces.joinToString(", ") }
                                    div("buttons") {
                                        button("button is-small") {
                                            autoFASvg("fa-user-pen")
                                            +"Edit"
                                            disabled(true)
                                            clicks handledBy {
                                                window.location.href = "/#/user/edit?username=${user.name}"
                                            }
                                        }
                                        button("button is-small") {
                                            autoFASvg("fa-trash-alt", size = 12)
                                            +"Delete"
                                            disabled(true)
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
            coroutineScope.launchJob {
                httpRequest("${backendVersion.DELETE_USER}${selectedUser.current!!.name}").auth()
                    .octetType().delete()
                infoToast("Deleted")
                window.location.reload()
            }
            infoToast("Deleting...")
        }
    }
}