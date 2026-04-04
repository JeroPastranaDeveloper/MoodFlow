package com.jero.designsystem.theme

import androidx.compose.ui.graphics.Color

object NoteColors {

    val palette: List<Long> = listOf(
        0L,
        0xFFFFF9C4L,
        0xFFC8E6C9L,
        0xFFBBDEFBL,
        0xFFF8BBD0L,
        0xFFE1BEE7L,
        0xFFFFE0B2L,
        0xFFFFCDD2L,
    )

    fun toComposeColor(colorLong: Long): Color =
        if (colorLong == 0L) Color.White else Color(colorLong.toInt())
}
