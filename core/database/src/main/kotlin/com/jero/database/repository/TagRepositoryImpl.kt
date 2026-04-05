package com.jero.database.repository

import android.util.Log
import com.example.domain.providers.StringsProvider
import com.example.domain.repository.TagRepository
import com.jero.core.designsystem.R
import com.jero.core.model.PERSONAL_TAG_ID
import com.jero.core.model.Tag
import com.jero.core.model.UNTAGGED_TAG_ID
import com.jero.database.datasource.TagDataSource
import com.jero.database.mapper.toDomain
import com.jero.database.mapper.toDto
import com.jero.database.mapper.toEntity
import com.jero.database.sync.NotesSyncManager
import com.jero.localdatabase.dao.NoteDao
import com.jero.localdatabase.dao.TagDao
import com.jero.localdatabase.model.PendingTagDeletionEntity
import com.jero.localdatabase.model.TagEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class TagRepositoryImpl(
    private val tagDataSource: TagDataSource,
    private val tagDao: TagDao,
    private val noteDao: NoteDao,
    private val syncManager: NotesSyncManager,
    private val strings: StringsProvider,
) : TagRepository {

    override fun getTagsFlow(userId: String): Flow<List<Tag>> =
        tagDao.getTagsFlow(userId).map { entities ->
            entities
                .map { it.toDomain() }
                .sortedWith(compareBy({ it.id != UNTAGGED_TAG_ID }, { it.name }))
        }

    override suspend fun createTag(name: String, userId: String): Result<Tag> = try {
        if (tagDao.tagNameExists(name, userId)) {
            return Result.failure(Exception(strings(R.string.error_tag_name_exists)))
        }
        val tag = TagEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            userId = userId,
            pendingSync = true,
        )
        tagDao.insertTag(tag)
        syncManager.scheduleSync()
        Result.success(tag.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateTag(tag: Tag): Result<Unit> = try {
        if (tagDao.tagNameExists(tag.name, tag.userId, excludeId = tag.id)) {
            return Result.failure(Exception(strings(R.string.error_tag_name_exists)))
        }
        tagDao.insertTag(tag.toEntity().copy(pendingSync = true))
        syncManager.scheduleSync()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteTag(tagId: String, userId: String): Result<Unit> = try {
        // Re-assign notes that would be left without tags
        val affectedNoteIds = tagDao.getNoteIdsForTag(tagId)
        affectedNoteIds.forEach { noteId ->
            val remainingTagIds = tagDao.getTagIdsForNote(noteId).filter { it != tagId }
            val finalTagIds = remainingTagIds.ifEmpty { listOf(UNTAGGED_TAG_ID) }
            tagDao.replaceTagsForNote(noteId, finalTagIds)
            noteDao.markAsPendingSync(noteId)
        }

        tagDao.deleteCrossRefsForTag(tagId)
        tagDao.deleteTag(tagId)
        tagDao.insertPendingTagDeletion(PendingTagDeletionEntity(tagId = tagId, userId = userId))
        syncManager.scheduleSync()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun assignTagsToNote(
        noteId: String,
        tagIds: List<String>,
        userId: String,
    ): Result<Unit> = try {
        tagDao.replaceTagsForNote(noteId, tagIds)
        noteDao.markAsPendingSync(noteId)
        syncManager.scheduleSync()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTagsForNote(noteId: String): List<Tag> =
        tagDao.getTagIdsForNote(noteId).mapNotNull { tagId ->
            tagDao.getTagById(tagId)?.toDomain()
        }

    override suspend fun seedDefaultTags(userId: String) {
        val existing = tagDao.getTags(userId).map { it.id }
        val toInsert = buildList {
            if (UNTAGGED_TAG_ID !in existing) add(
                TagEntity(id = UNTAGGED_TAG_ID, name = "Sin etiqueta", userId = userId, pendingSync = true)
            )
            if (PERSONAL_TAG_ID !in existing) add(
                TagEntity(id = PERSONAL_TAG_ID, name = "Personales", userId = userId, pendingSync = true)
            )
        }
        if (toInsert.isNotEmpty()) {
            tagDao.insertTags(toInsert)
            syncManager.scheduleSync()
        }
    }

    override suspend fun syncPendingChanges(userId: String) {
        try {
            tagDao.getPendingTags(userId).forEach { tag ->
                tagDataSource.upsertTag(tag.toDto(), userId)
                    .onSuccess { tagDao.markTagAsSynced(tag.id) }
                    .onFailure { Log.e("TagRepository", "Failed to sync tag ${tag.id}", it) }
            }
            tagDao.getPendingTagDeletions(userId).forEach { deletion ->
                tagDataSource.deleteTag(deletion.tagId, userId)
                    .onSuccess { tagDao.removePendingTagDeletion(deletion.tagId) }
                    .onFailure { Log.e("TagRepository", "Failed to delete tag ${deletion.tagId}", it) }
            }
        } catch (e: Exception) {
            Log.e("TagRepository", "Error syncing tags", e)
        }
    }
}
