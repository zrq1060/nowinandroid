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
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceFtsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [NewsResourceFtsEntity] access.
 * 用于[NewsResourceFtsEntity]访问的DAO。
 */
@Dao
// 新闻资源-Fts表（newsResourcesFts）的操作，Fts表用于快速查询。包含：插入所有数据、查询所有与query匹配的、获取此表的数量。
interface NewsResourceFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // 插入所有，如果冲突，则替换。
    suspend fun insertAll(newsResources: List<NewsResourceFtsEntity>)

    @Query("SELECT newsResourceId FROM newsResourcesFts WHERE newsResourcesFts MATCH :query")
    // 搜索所有与query匹配的新闻，并获取其newsResourceId列表。
    fun searchAllNewsResources(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM newsResourcesFts")
    // 获取此表的数量
    fun getCount(): Flow<Int>
}
