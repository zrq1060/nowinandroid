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

import com.google.samples.apps.nowinandroid.core.model.data.SearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for the search feature.
 * 用于搜索功能的数据层接口。
 */
// 搜索内容的仓库，包含为：搜索内容填充fts表、搜索查询并返回其总体（Topics+NewsResources）结果、获取搜索内容的数量。
interface SearchContentsRepository {

    /**
     * Populate the fts tables for the search contents.
     * 为搜索内容-填充fts表。
     */
    // 搜索内容填充fts表
    suspend fun populateFtsData()

    /**
     * Query the contents matched with the [searchQuery] and returns it as a [Flow] of [SearchResult]
     * 查询与[searchQuery]匹配的内容，并将其作为[SearchResult]的[Flow]返回
     */
    // 搜索查询并返回其总体（Topics+NewsResources）结果
    fun searchContents(searchQuery: String): Flow<SearchResult>

    // 获取搜索内容的数量
    fun getSearchContentsCount(): Flow<Int>
}
