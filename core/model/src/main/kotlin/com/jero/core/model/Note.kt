package com.jero.core.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Immutable
@Parcelize
@Serializable
data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: Long = 0,
    val pinned: Boolean = false,
    val color: Long = 0L,
    val userId: String = "",
    val pendingSync: Boolean = false,
) : Parcelable

fun Note.hasContentWithoutId(): Boolean = (title.isNotEmpty() || content.isNotEmpty()) && id.isBlank()
