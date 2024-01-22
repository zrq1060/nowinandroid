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

package com.google.samples.apps.nowinandroid.core.data.repository

import com.google.samples.apps.nowinandroid.core.data.model.RecentSearchQuery
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for the recent searches.
 * 最近搜索的数据层接口。
 */
// RecentSearchQuery的仓库，包含：获取最近查询的列表的Flow、插入或替换（更新了时间）最近查询、清空最近搜索。
interface RecentSearchRepository {

    /**
     * Get the recent search queries up to the number of queries specified as [limit].
     * 获取最近的搜索查询，查询数量不超过指定的[limit]限制。
     */
    // 获取最近查询的列表的Flow
    fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>>

    /**
     * Insert or replace the [searchQuery] as part of the recent searches.
     * 插入或替换[searchQuery]作为最近搜索的一部分。
     */
    // 插入或替换（更新了时间）最近查询
    suspend fun insertOrReplaceRecentSearch(searchQuery: String)

    /**
     * Clear the recent searches.
     * 清除最近的搜索。
     */
    // 清空最近搜索
    suspend fun clearRecentSearches()
}
