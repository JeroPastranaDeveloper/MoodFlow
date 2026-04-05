package com.jero.database.datasource

import com.jero.database.model.TagDto

interface TagDataSource {
    suspend fun upsertTag(tag: TagDto, userId: String): Result<Unit>
    suspend fun deleteTag(tagId: String, userId: String): Result<Unit>
    suspend fun getAllTags(userId: String): Result<List<TagDto>>
}
