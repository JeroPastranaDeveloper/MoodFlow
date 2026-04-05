package com.example.domain.repository

import com.jero.core.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getTagsFlow(userId: String): Flow<List<Tag>>
    suspend fun createTag(name: String, userId: String): Result<Tag>
    suspend fun updateTag(tag: Tag): Result<Unit>
    suspend fun deleteTag(tagId: String, userId: String): Result<Unit>
    suspend fun assignTagsToNote(noteId: String, tagIds: List<String>, userId: String): Result<Unit>
    suspend fun getTagsForNote(noteId: String): List<Tag>
    suspend fun seedDefaultTags(userId: String)
    suspend fun syncPendingChanges(userId: String)
}
