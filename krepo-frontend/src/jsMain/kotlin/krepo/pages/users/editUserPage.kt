/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:29 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.pages.users

import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import krepo.backendVersion
import krepo.components.infoToast
import krepo.components.showDialog
import krepo.components.warningToast
import krepo.coroutineScope
import krepo.currentPath
import krepo.enums.CheckImplType
import krepo.util.*
import krepo.util.file.checkSession
import krepo.util.string.validateEmail

fun RenderContext.editUserPage() {
    checkSession {
        val user = currentPath.split("?").last().split("&").first().split("=").last()
        val email = storeOf("")
        val password = storeOf("")
        val showUpdateUserDialog = storeOf(false)
        div("section") {
            div("container") {
                inlineStyle("max-width: 600px;")
                h3("title is-3 has-text-centered mb-6") {
                    i("fas fa-user-pen mr-2") {}
                    +"Update user info"
                }
                div("box mx-auto") {
                    div("columns is-multiline") {
                        div("column is-full") {
                            div("field") {
                                label("label") { +"Username" }
                                div("control has-icons-left") {
                                    input("input") {
                                        disabled(true)
                                        type("text")
                                        placeholder("Input username here")
                                        value(user)
                                    }
                                    span("icon is-small is-left") {
                                        i("fas fa-user") {}
                                    }
                                }
                            }
                        }
                        div("column is-full") {
                            div("field") {
                                label("label") { +"Email" }
                                div("control has-icons-left") {
                                    input("input") {
                                        type("email")
                                        placeholder("Input email address here")
                                        value(email.data)
                                        changes.values() handledBy email.update
                                    }
                                    span("icon is-small is-left") {
                                        i("fas fa-envelope") {}
                                    }
                                }
                            }
                        }
                        div("column is-full") {
                            div("field") {
                                label("label") { +"Password" }
                                div("control has-icons-left") {
                                    input("input") {
                                        type("password")
                                        placeholder("Input password here")
                                        value(password.data)
                                        changes.values() handledBy password.update
                                    }
                                    span("icon is-small is-left") {
                                        i("fas fa-lock") {}
                                    }
                                }
                            }
                            div("column is-full") {
                                div("field is-grouped is-grouped-right mt-5") {
                                    div("control") {
                                        button("button is-link") {
                                            i("fa-solid fa-pen-to-square mr-2") {}
                                            +"Update user info"
                                            clicks handledBy { showUpdateUserDialog.update(true) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        showDialog(showUpdateUserDialog, "Update User Info", "Do you want to update the user info?", {}) {
            if (password.current.isBlank() || email.current.isBlank()) {
                warningToast("Please fill in all fields")
            } else {
                if (validateEmail(email)) {
                    val requestBody = mapOf(
                        "name" to user,
                        "password" to password.current,
                        "email" to email.current
                    )
                    coroutineScope.launch {
                        httpRequest(backendVersion.MODIFY_USER)
                            .auth().acceptJson().jsonContentType()
                            .setBody(requestBody).put().checkImpl(CheckImplType.Toast) {
                                infoToast("User info updated")
                                window.location.href = "/#/user/manage"
                            }
                    }
                }
            }
        }
    }
}