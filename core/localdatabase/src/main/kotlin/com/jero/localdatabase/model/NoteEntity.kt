package com.jero.localdatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteEntity(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: Long = 0,
    val pinned: Boolean = false,
    val color: Long = 0L,
    val userId: String = "",
    val pendingSync: Boolean = false,
    val deletedAt: Long? = null,
)
