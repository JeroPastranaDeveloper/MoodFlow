package com.jero.core.model

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: Long = 0,
    val pinned: Boolean = false,
    val userId: String = "",
    val pendingSync: Boolean = false,
)
