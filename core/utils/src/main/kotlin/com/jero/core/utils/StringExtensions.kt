package com.jero.core.utils

import android.text.Html

fun emptyString(): String = ""

private const val BULLET_MARKER = "\uE001"
private const val NEWLINE_MARKER = "\uE002"

fun String.htmlToPlainText(): String =
    this
        .replace(Regex("<li[^>]*>\\s*(&bullet;)?\\s*", RegexOption.IGNORE_CASE), BULLET_MARKER)
        .replace(Regex("</p>|</li>|<br\\s*/?>", RegexOption.IGNORE_CASE), NEWLINE_MARKER)
        .let { Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT).toString() }
        .replace(BULLET_MARKER, "• ")
        .replace(NEWLINE_MARKER, "\n")
        .replace(Regex("\n{2,}"), "\n")
        .trim()
