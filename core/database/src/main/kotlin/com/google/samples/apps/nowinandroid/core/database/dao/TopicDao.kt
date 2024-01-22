/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [TopicEntity] access
 * 用于[TopicEntity]访问的DAO
 */
@Dao
// 主题表（topics）的操作。包含：获取指定topicId或者指定topicIds的主题（异步）、获取全部的主题（同步、异步）、插入或忽略（如果存在则忽略）指定topicEntities、更新插入（如果存在则替换）指定topicEntities、删除指定ids的主题。
interface TopicDao {
    @Query(
        value = """
        SELECT * FROM topics
        WHERE id = :topicId
    """,
    )
    // 获取指定topicId的TopicEntity（异步）
    fun getTopicEntity(topicId: String): Flow<TopicEntity>

    @Query(value = "SELECT * FROM topics")
    // 获取所有的TopicEntity（异步）
    fun getTopicEntities(): Flow<List<TopicEntity>>

    @Query(value = "SELECT * FROM topics")
    // 获取所有的TopicEntity（同步，只获取一次）
    suspend fun getOneOffTopicEntities(): List<TopicEntity>

    @Query(
        value = """
        SELECT * FROM topics
        WHERE id IN (:ids)
    """,
    )
    // 获取指定ids的所有TopicEntity（异步）
    fun getTopicEntities(ids: Set<String>): Flow<List<TopicEntity>>

    /**
     * Inserts [topicEntities] into the db if they don't exist, and ignores those that do
     * 如果[topicEntities]不存在，则插入到数据库中，并忽略存在的
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    // 插入或忽略（如果存在则忽略）指定topicEntities的TopicEntity
    suspend fun insertOrIgnoreTopics(topicEntities: List<TopicEntity>): List<Long>

    /**
     * Inserts or updates [entities] in the db under the specified primary keys
     * 在指定的主键下插入或更新数据库中的[entities]
     */
    @Upsert
    // 更新插入（如果存在则替换）指定entities的TopicEntity
    suspend fun upsertTopics(entities: List<TopicEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     * 删除数据库中与指定[ids]匹配的行。
     */
    @Query(
        value = """
            DELETE FROM topics
            WHERE id in (:ids)
        """,
    )
    // 删除指定ids的TopicEntity
    suspend fun deleteTopics(ids: List<String>)
}
