package com.jero.database.datasource

import com.google.firebase.database.FirebaseDatabase
import com.jero.database.model.TagDto
import kotlinx.coroutines.tasks.await

class TagDataSourceImpl(
    private val database: FirebaseDatabase,
) : TagDataSource {

    private fun getUserTagsRef(userId: String) =
        database.reference.child("users").child(userId).child("tags")

    override suspend fun upsertTag(tag: TagDto, userId: String): Result<Unit> = try {
        getUserTagsRef(userId).child(tag.id).setValue(tag).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteTag(tagId: String, userId: String): Result<Unit> = try {
        getUserTagsRef(userId).child(tagId).removeValue().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getAllTags(userId: String): Result<List<TagDto>> = try {
        val snapshot = getUserTagsRef(userId).get().await()
        val tags = snapshot.children.mapNotNull { it.getValue(TagDto::class.java) }
        Result.success(tags)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
