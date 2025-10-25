package com.jero.localdatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PendingDeletionEntity(
    @PrimaryKey val noteId: String,
    val userId: String,
    val date: Long = System.currentTimeMillis()
)