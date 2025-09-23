/*
 * Copyright © 2025 RTAkland
 * Date: 9/24/25, 6:50 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.util

import io.ktor.http.ContentType

fun String.contentTypeMapper(): ContentType {
    val suffix = this.split(".").last()
   return when(suffix) {
       "svg" -> ContentType.Image.SVG
       "png" -> ContentType.Image.PNG
       "jpg", "jpeg" -> ContentType.Image.JPEG
       "css" -> ContentType.Text.CSS
       "html" -> ContentType.Text.Html
       else -> ContentType.Text.Plain
   }
}