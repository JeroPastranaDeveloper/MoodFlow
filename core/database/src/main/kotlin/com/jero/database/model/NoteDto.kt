package com.jero.database.model

data class NoteDto(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: Long = 0,
    val pinned: Boolean = false,
    val color: Long = 0L,
    val userId: String = "",
    val pendingSync: Boolean = false,
)
