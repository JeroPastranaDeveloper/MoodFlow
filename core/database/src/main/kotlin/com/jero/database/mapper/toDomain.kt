package com.jero.database.mapper

import com.jero.core.model.Note
import com.jero.core.model.Tag
import com.jero.core.model.UNTAGGED_TAG_ID
import com.jero.database.model.NoteDto
import com.jero.database.model.TagDto
import com.jero.localdatabase.model.NoteEntity
import com.jero.localdatabase.model.NoteWithTags
import com.jero.localdatabase.model.TagEntity

// ── Note mappers ──────────────────────────────────────────────────────────────

fun NoteDto.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    date = date,
    pinned = pinned,
    color = color,
    userId = userId,
    pendingSync = pendingSync,
    deletedAt = deletedAt,
    tagIds = tagIds.keys.toList().ifEmpty { listOf(UNTAGGED_TAG_ID) },
)

fun NoteWithTags.toDomain(): Note = Note(
    id = note.id,
    title = note.title,
    content = note.content,
    date = note.date,
    pinned = note.pinned,
    color = note.color,
    userId = note.userId,
    pendingSync = note.pendingSync,
    deletedAt = note.deletedAt,
    tagIds = tags.map { it.id }.ifEmpty { listOf(UNTAGGED_TAG_ID) },
)

fun NoteEntity.toDto(tagIds: List<String> = emptyList()): NoteDto = NoteDto(
    id = id,
    title = title,
    content = content,
    date = date,
    pinned = pinned,
    color = color,
    userId = userId,
    pendingSync = pendingSync,
    deletedAt = deletedAt,
    tagIds = tagIds.associateWith { true },
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
    deletedAt = deletedAt,
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
    deletedAt = deletedAt,
)

// ── Tag mappers ───────────────────────────────────────────────────────────────

fun TagEntity.toDomain(): Tag = Tag(
    id = id,
    name = name,
    userId = userId,
    pendingSync = pendingSync,
)

fun Tag.toEntity(): TagEntity = TagEntity(
    id = id,
    name = name,
    userId = userId,
    pendingSync = pendingSync,
)

fun TagEntity.toDto(): TagDto = TagDto(id = id, name = name, userId = userId)
