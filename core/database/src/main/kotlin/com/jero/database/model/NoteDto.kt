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
    val deletedAt: Long? = null,
    // Tag IDs stored as a Firebase set: { "tagId": true }
    val tagIds: Map<String, Boolean> = emptyMap(),
)
