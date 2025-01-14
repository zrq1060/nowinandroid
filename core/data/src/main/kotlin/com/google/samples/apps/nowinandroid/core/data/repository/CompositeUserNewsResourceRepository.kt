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
import com.google.samples.apps.nowinandroid.core.model.data.mapToUserNewsResources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implements a [UserNewsResourceRepository] by combining a [NewsRepository] with a
 * [UserDataRepository].
 * 通过组合[NewsRepository]和[UserDataRepository]实现[UserNewsResourceRepository]。
 */
// 用户新闻资源的仓库（UserNewsResourceRepository）-组合实现。使用NewsRepository、UserDataRepository实现。
class CompositeUserNewsResourceRepository @Inject constructor(
    val newsRepository: NewsRepository,
    val userDataRepository: UserDataRepository,
) : UserNewsResourceRepository {

    /**
     * Returns available news resources (joined with user data) matching the given query.
     * 返回与给定查询匹配的可用新闻资源(与用户数据结合)。
     */
    // 获取全部UserNewsResource，可过滤。
    override fun observeAll(
        query: NewsResourceQuery,
    ): Flow<List<UserNewsResource>> =
        newsRepository.getNewsResources(query)
            .combine(userDataRepository.userData) { newsResources, userData ->
                // 根据userData，将NewsResource列表转为UserNewsResource列表。
                newsResources.mapToUserNewsResources(userData)
            }

    /**
     * Returns available news resources (joined with user data) for the followed topics.
     * 返回以下主题的可用新闻资源(与用户数据连接)。
     */
    // 获取全部用户关注的UserNewsResource。
    override fun observeAllForFollowedTopics(): Flow<List<UserNewsResource>> =
        userDataRepository.userData.map { it.followedTopics }.distinctUntilChanged()
            .flatMapLatest { followedTopics ->
                // 已关注的-主题Id列表
                when {
                    // id列表为空，则主题列表为空。
                    followedTopics.isEmpty() -> flowOf(emptyList())
                    // id列表不为空，则获取主题列表。
                    else -> observeAll(NewsResourceQuery(filterTopicIds = followedTopics))
                }
            }

    // 获取全部用户加入书签的UserNewsResource。
    override fun observeAllBookmarked(): Flow<List<UserNewsResource>> =
        userDataRepository.userData.map { it.bookmarkedNewsResources }.distinctUntilChanged()
            .flatMapLatest { bookmarkedNewsResources ->
                // 已加入书签的-新闻资源Id列表
                when {
                    // id列表为空，则主题列表为空。
                    bookmarkedNewsResources.isEmpty() -> flowOf(emptyList())
                    // id列表不为空，则获取主题列表。
                    else -> observeAll(NewsResourceQuery(filterNewsIds = bookmarkedNewsResources))
                }
            }
}
