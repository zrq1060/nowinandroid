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

import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [UserNewsResource]
 * [UserNewsResource]的数据层实现
 */
// UserNewsResource（NewsResource + UserData）的仓库，包含：获取所有（全部、用户关注的、用户加入书签的）的UserNewsResource列表。
interface UserNewsResourceRepository {
    /**
     * Returns available news resources as a stream.
     * 以流的形式返回-可用的新闻资源。
     */
    // 获取全部UserNewsResource，可过滤。
    fun observeAll(
        query: NewsResourceQuery = NewsResourceQuery(
            filterTopicIds = null,
            filterNewsIds = null,
        ),
    ): Flow<List<UserNewsResource>>

    /**
     * Returns available news resources for the user's followed topics as a stream.
     * 以流的形式返回-用户关注的主题-可用的新闻资源。
     */
    // 获取全部用户关注的UserNewsResource。
    fun observeAllForFollowedTopics(): Flow<List<UserNewsResource>>

    /**
     * Returns the user's bookmarked news resources as a stream.
     * 以流的形式返回-用户书签的-新闻资源。
     */
    // 获取全部用户加入书签的UserNewsResource。
    fun observeAllBookmarked(): Flow<List<UserNewsResource>>
}
