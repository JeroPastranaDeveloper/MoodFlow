package com.jero.database.model

data class NoteEntity(
    val id: String,
    val title: String,
    val content: String,
    val date: Long,
    val userId: String,
)
