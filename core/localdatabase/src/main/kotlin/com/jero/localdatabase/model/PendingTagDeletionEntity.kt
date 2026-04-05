package com.jero.localdatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_tag_deletions")
data class PendingTagDeletionEntity(
    @PrimaryKey val tagId: String,
    val userId: String,
)
