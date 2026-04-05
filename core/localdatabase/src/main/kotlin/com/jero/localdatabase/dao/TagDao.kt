package com.jero.localdatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jero.localdatabase.model.NoteTagCrossRef
import com.jero.localdatabase.model.PendingTagDeletionEntity
import com.jero.localdatabase.model.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    // ── Tags ──────────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)

    @Query("SELECT * FROM tags WHERE userId = :userId ORDER BY name ASC")
    fun getTagsFlow(userId: String): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE userId = :userId ORDER BY name ASC")
    suspend fun getTags(userId: String): List<TagEntity>

    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getTagById(id: String): TagEntity?

    @Query("SELECT * FROM tags WHERE pendingSync = 1 AND userId = :userId")
    suspend fun getPendingTags(userId: String): List<TagEntity>

    @Query("UPDATE tags SET pendingSync = 0 WHERE id = :id")
    suspend fun markTagAsSynced(id: String)

    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun deleteTag(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM tags WHERE name = :name AND userId = :userId AND id != :excludeId)")
    suspend fun tagNameExists(name: String, userId: String, excludeId: String = ""): Boolean

    // ── Cross-refs ────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: NoteTagCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<NoteTagCrossRef>)

    @Query("DELETE FROM note_tag_cross_ref WHERE noteId = :noteId AND tagId = :tagId")
    suspend fun deleteCrossRef(noteId: String, tagId: String)

    @Query("DELETE FROM note_tag_cross_ref WHERE noteId = :noteId")
    suspend fun deleteCrossRefsForNote(noteId: String)

    @Query("DELETE FROM note_tag_cross_ref WHERE tagId = :tagId")
    suspend fun deleteCrossRefsForTag(tagId: String)

    @Query("SELECT tagId FROM note_tag_cross_ref WHERE noteId = :noteId")
    suspend fun getTagIdsForNote(noteId: String): List<String>

    @Query("SELECT noteId FROM note_tag_cross_ref WHERE tagId = :tagId")
    suspend fun getNoteIdsForTag(tagId: String): List<String>

    @Transaction
    suspend fun replaceTagsForNote(noteId: String, tagIds: List<String>) {
        deleteCrossRefsForNote(noteId)
        insertCrossRefs(tagIds.map { NoteTagCrossRef(noteId = noteId, tagId = it) })
    }

    // ── Pending tag deletions ─────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingTagDeletion(deletion: PendingTagDeletionEntity)

    @Query("SELECT * FROM pending_tag_deletions WHERE userId = :userId")
    suspend fun getPendingTagDeletions(userId: String): List<PendingTagDeletionEntity>

    @Query("DELETE FROM pending_tag_deletions WHERE tagId = :tagId")
    suspend fun removePendingTagDeletion(tagId: String)
}
