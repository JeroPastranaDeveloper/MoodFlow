package com.jero.database.mapper

import com.jero.core.model.Note
import com.jero.database.model.NoteDto
import com.jero.localdatabase.model.NoteEntity

fun NoteDto.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    date = date,
    pinned = pinned,
    color = color,
    userId = userId,
    pendingSync = pendingSync,
)

fun NoteEntity.toDto(): NoteDto = NoteDto(
    id = id,
    title = title,
    content = content,
    date = date,
    pinned = pinned,
    color = color,
    userId = userId,
    pendingSync = pendingSync,
)

fun NoteDto.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    date = date,
    pinned = pinned,
    color = color,
    userId = userId,
    pendingSync = pendingSync,
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    date = date,
    pinned = pinned,
    color = color,
    userId = userId,
    pendingSync = pendingSync,
)

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    date = date,
    pinned = pinned,
    color = color,
    userId = userId,
    pendingSync = pendingSync,
)
