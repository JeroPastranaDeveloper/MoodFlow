package com.jero.core.utils

import android.text.Html

fun emptyString(): String = ""

fun String.htmlToPlainText(): String =
    Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT).toString().trim()
