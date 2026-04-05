package com.jero.localdatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val userId: String = "",
    val pendingSync: Boolean = false,
)
