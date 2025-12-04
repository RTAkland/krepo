/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:29 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.pages.users

import dev.fritz2.core.*
import kotlinx.browser.window
import krepo.backendVersion
import krepo.components.infoToast
import krepo.components.showDialog
import krepo.components.warningToast
import krepo.coroutineScope
import krepo.currentPath
import krepo.entity.maven.token.GrantPublishTokenRequest
import krepo.entity.maven.token.GrantPublishTokenResponse
import krepo.util.auth
import krepo.util.byte.fromProtoBuf
import krepo.util.file.LocalStorage
import krepo.util.file.checkPermission
import krepo.util.httpRequest
import krepo.util.img.autoFASvg
import krepo.util.launchJob
import krepo.util.setOctetBody
import krepo.util.string.validateEmail

fun RenderContext.UserSettingsPage() = checkPermission {
    val user = currentPath.split("?").last().split("&").first().split("=").last()
    val email = storeOf("")
    val password = storeOf("")
    val showUpdateUserDialog = storeOf(false)
    val showGenerateNewPublishTokenDialogStore = storeOf(false)
    val generatedPublishTokenResponseStore = storeOf<GrantPublishTokenResponse?>(null)
    div("section") {
        div("container") {
            inlineStyle("max-width: 600px;")
            h3("title is-3 has-text-centered mb-6") {
                autoFASvg("fa-user-pen", "mr-2 mb-2", size = 32)
                +"User settings"
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
                                span("icon is-small is-left") { autoFASvg("fa-user", "") }
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
                                span("icon is-small is-left") { autoFASvg("fa-envelope", "") }
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
                                span("icon is-small is-left") { autoFASvg("fa-lock", "") }
                            }
                        }
                    }
                    div("column is-full") {
                        div("field is-grouped is-grouped-right mt-5") {
                            button("button") {
                                autoFASvg("fa-key")
                                +"Generate publish token"
                                clicks handledBy { showGenerateNewPublishTokenDialogStore.update(true) }
                            }
                            button("button") updateInfoButton@{
                                autoFASvg("fa-pen-to-square")
                                +"Update user info"
                                disabled(true)
                                clicks handledBy { showUpdateUserDialog.update(true) }
                            }
                        }
                    }
                }
                generatedPublishTokenResponseStore.data.render {
                    if (it != null) {
                        window.navigator.clipboard.writeText(it.token)
                        infoToast("New publish token copied")
                    }
                }
            }
        }
    }
    showDialog(
        showGenerateNewPublishTokenDialogStore,
        "Generate new publish token",
        "Generate a new publish token and copy it?",
        {
            span("mt-2") { i { +"Previous tokens will not be revoked." } }
        }) {
        coroutineScope.launchJob {
            infoToast("Generating...")
            val resp = httpRequest(backendVersion.GRANT_PUBLISH_TOKEN)
                .auth().setOctetBody(GrantPublishTokenRequest(LocalStorage.USERNAME!!))
                .post().arrayBuffer().fromProtoBuf<GrantPublishTokenResponse>()
            generatedPublishTokenResponseStore.update(resp)
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
                coroutineScope.launchJob {
                    httpRequest(backendVersion.MODIFY_USER)
                        .auth().setOctetBody(requestBody).put()
                    infoToast("User info updated")
                    window.location.href = "/#/user/manage"
                }
            }
        }
    }
}
