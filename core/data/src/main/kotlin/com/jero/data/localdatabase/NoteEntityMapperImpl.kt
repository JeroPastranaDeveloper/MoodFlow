package com.jero.data.localdatabase

import com.jero.core.model.Note
import com.jero.localdatabase.mapper.NoteEntityMapper
import com.jero.localdatabase.model.NoteEntity

object NoteEntityMapperImpl : NoteEntityMapper<Note, NoteEntity> {
    override fun asEntity(domain: Note): NoteEntity = NoteEntity(
        id = domain.id,
        title = domain.title,
        content = domain.content,
        date = domain.date,
        pinned = domain.pinned,
        userId = domain.userId,
    )

    override fun asDomain(entity: NoteEntity): Note = Note(
        id = entity.id,
        title = entity.title,
        content = entity.content,
        date = entity.date,
        pinned = entity.pinned,
        userId = entity.userId,
    )
}

fun NoteEntity.asDomain(): Note = NoteEntityMapperImpl.asDomain(this)
fun Note.asEntity(): NoteEntity = NoteEntityMapperImpl.asEntity(this)
