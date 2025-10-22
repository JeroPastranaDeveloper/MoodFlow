package com.jero.database.mapper

import com.jero.core.model.Note
import com.jero.database.model.NoteEntity

fun NoteEntity.toDomain(): Note = Note(
        id = id,
        title = title,
        content = content,
        date = date,
        userId = userId
    )

fun Note.toEntity(): NoteEntity = NoteEntity(
        id = id,
        title = title,
        content = content,
        date = date,
        userId = userId
    )
