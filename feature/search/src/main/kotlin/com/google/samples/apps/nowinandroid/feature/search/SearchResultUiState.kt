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

package com.google.samples.apps.nowinandroid.feature.search

import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource

// 搜索结果-UiState
sealed interface SearchResultUiState {
    // 加载中
    data object Loading : SearchResultUiState

    /**
     * The state query is empty or too short. To distinguish the state between the
     * (initial state or when the search query is cleared) vs the state where no search
     * result is returned, explicitly define the empty query state.
     * 状态查询为空或太短。要区分(初始状态或清除搜索查询时的状态)和不返回搜索结果的状态，需要显式定义空查询状态。
     */
    // 空查询，查询为空或者查询内容太短。
    data object EmptyQuery : SearchResultUiState

    // 加载失败
    data object LoadFailed : SearchResultUiState

    // 加载成功
    data class Success(
        val topics: List<FollowableTopic> = emptyList(),
        val newsResources: List<UserNewsResource> = emptyList(),
    ) : SearchResultUiState {
        // 是否为空
        fun isEmpty(): Boolean = topics.isEmpty() && newsResources.isEmpty()
    }

    /**
     * A state where the search contents are not ready. This happens when the *Fts tables are not
     * populated yet.
     * 搜索内容尚未准备好的状态。当*Fts表尚未填充时，就会发生这种情况。
     */
    // 搜索没准备好
    data object SearchNotReady : SearchResultUiState
}
