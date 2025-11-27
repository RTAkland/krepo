/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:29 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.pages.settings

import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import krepo.backendVersion
import krepo.components.infoToast
import krepo.components.showDialog
import krepo.coroutineScope
import krepo.entity.FrontendConfig
import krepo.enums.CheckImplType
import krepo.frontendConfig
import krepo.util.*
import krepo.util.file.checkPermission

fun RenderContext.settingPage() {
    checkPermission {
        val pageTitleStore = storeOf(frontendConfig.pageTitle)
        val icpLicenseStore = storeOf(frontendConfig.icpLicense)
        val descriptionStore = storeOf(frontendConfig.description)
        val copyrightStore = storeOf(frontendConfig.copyright)
        val showSubmitSettingDialog = storeOf(false)
        val showResetFrontConfigDialog = storeOf(false)
        coroutineScope.launch {
            div("container") {
                inlineStyle("max-width: 60%;")
                h3("title is-3") { +"Update frontend settings" }
                div("box") {
                    div("field") {
                        label("label") { +"Title" }
                        div("control") {
                            input("input") {
                                type("text")
                                placeholder("Fill in the frontend title here")
                                value(pageTitleStore.current)
                                changes.values() handledBy pageTitleStore.update
                            }
                        }
                    }
                    div("field") {
                        label("label") { +"Description" }
                        div("control") {
                            input("input") {
                                type("text")
                                placeholder("Fill in the frontend description here")
                                value(descriptionStore.current ?: "")
                                changes.values() handledBy descriptionStore.update
                            }
                        }
                    }
                    div("field") {
                        label("label") { +"Copyright" }
                        div("control") {
                            input("input") {
                                type("text")
                                placeholder("Fill in the frontend copyright here")
                                value(copyrightStore.current)
                                changes.values() handledBy copyrightStore.update
                            }
                        }
                    }
                    div("field") {
                        label("label") { +"ICP License" }
                        div("control") {
                            input("input") {
                                type("text")
                                placeholder("Fill in the frontend ICP License here")
                                value(icpLicenseStore.current ?: "")
                                changes.values() handledBy icpLicenseStore.update
                            }
                        }
                    }
                    div("is-grouped is-right buttons") {
                        div("control") {
                            button("button is-danger") {
                                img("mr-2") { src("/assets/img/reset.svg") }
                                +"Reset"
                                clicks handledBy { showResetFrontConfigDialog.update(true) }
                            }
                        }
                        div("control") {
                            button("button is-info") {
                                img("mr-2") { src("/assets/img/save.svg") }
                                +"Save"
                                clicks handledBy { showSubmitSettingDialog.update(true) }
                            }
                        }
                    }
                }
            }
        }
        showDialog(showSubmitSettingDialog, "Saving the changes", "Do you want to save these changes?", {}) {
            val pageTitle = pageTitleStore.current
            val icpLicense = icpLicenseStore.current
            val description = descriptionStore.current
            val copyright = copyrightStore.current
            coroutineScope.launch {
                httpRequest(backendVersion.MODIFY_FRONTEND_CONFIG)
                    .auth().acceptJson().jsonContentType()
                    .setBody(FrontendConfig(pageTitle, icpLicense, description, copyright, false))
                    .put().checkImpl(CheckImplType.Toast) {
                        infoToast("Saved!")
                        window.location.reload()
                    }
            }
        }
        showDialog(showResetFrontConfigDialog, "Reset", "Do you want to reset the frontend settings?", {}) {
            coroutineScope.launch {
                httpRequest(backendVersion.RESET_FRONTEND_CONFIG)
                    .auth().acceptJson().jsonContentType()
                    .put().checkImpl(CheckImplType.Toast) {
                        infoToast("Rested!")
                        window.location.reload()
                    }
            }
        }
    }
}