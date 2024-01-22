/*
 * Copyright 2023 The Android Open Source Project
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
import androidx.room.Query
import androidx.room.Upsert
import com.google.samples.apps.nowinandroid.core.database.model.RecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [RecentSearchQueryEntity] access
 * 用于[RecentSearchQueryEntity]访问的DAO
 */
@Dao
// 最近搜索查询表（recentSearchQueries）的操作。包含：获取最近搜索的查询、更新插入最近搜索查询、清除最近搜索查询。
interface RecentSearchQueryDao {
    @Query(value = "SELECT * FROM recentSearchQueries ORDER BY queriedDate DESC LIMIT :limit")
    // 获取最近搜索的查询，并按照时间排序，并获取指定limit的数量个。
    fun getRecentSearchQueryEntities(limit: Int): Flow<List<RecentSearchQueryEntity>>

    @Upsert
    // 更新插入（有冲突则替换）最近搜索查询（RecentSearchQueryEntity）
    suspend fun insertOrReplaceRecentSearchQuery(recentSearchQuery: RecentSearchQueryEntity)

    @Query(value = "DELETE FROM recentSearchQueries")
    // 清除最近搜索查询，即清除表所有数据。
    suspend fun clearRecentSearchQueries()
}
