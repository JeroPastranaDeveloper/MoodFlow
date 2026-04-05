package com.jero.localdatabase.model

import androidx.room.Entity

@Entity(
    tableName = "note_tag_cross_ref",
    primaryKeys = ["noteId", "tagId"],
)
data class NoteTagCrossRef(
    val noteId: String,
    val tagId: String,
)
