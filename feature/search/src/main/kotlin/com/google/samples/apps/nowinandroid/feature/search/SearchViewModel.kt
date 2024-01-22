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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent.Param
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.data.repository.RecentSearchRepository
import com.google.samples.apps.nowinandroid.core.domain.GetRecentSearchQueriesUseCase
import com.google.samples.apps.nowinandroid.core.domain.GetSearchContentsCountUseCase
import com.google.samples.apps.nowinandroid.core.domain.GetSearchContentsUseCase
import com.google.samples.apps.nowinandroid.core.result.Result
import com.google.samples.apps.nowinandroid.core.result.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
// Search（搜索）屏-ViewModel
class SearchViewModel @Inject constructor(
    // 获取搜索内容（UserSearchResult）的用例
    getSearchContentsUseCase: GetSearchContentsUseCase,
    // 获取搜索内容数量的用例
    getSearchContentsCountUseCase: GetSearchContentsCountUseCase,
    // 最近搜索查询用例
    recentSearchQueriesUseCase: GetRecentSearchQueriesUseCase,
    // 最近搜索仓库
    private val recentSearchRepository: RecentSearchRepository,
    // SavedStateHandle
    private val savedStateHandle: SavedStateHandle,
    // 分析辅助
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    // 搜索查询的内容，搜索输入框内容改变就变。
    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    // Search结果的UiState，默认加载中。
    val searchResultUiState: StateFlow<SearchResultUiState> =
        getSearchContentsCountUseCase()
            .flatMapLatest { totalCount ->
                if (totalCount < SEARCH_MIN_FTS_ENTITY_COUNT) {
                    // 小于1个，搜索没准备好，返回SearchResultUiState.SearchNotReady。
                    flowOf(SearchResultUiState.SearchNotReady)
                } else {
                    // 大于1个，搜索准备好了，开始进行搜索查询。
                    searchQuery.flatMapLatest { query ->
                        if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                            // 查询的内容长度小于2，则认为没搜索，返回SearchResultUiState.EmptyQuery。
                            flowOf(SearchResultUiState.EmptyQuery)
                        } else {
                            // 查询的内容长度大于等于2，则认为是搜索，获取搜索内容。
                            getSearchContentsUseCase(query)
                                .asResult()
                                .map { result ->
                                    when (result) {
                                        // 成功，返回SearchResultUiState.Success。
                                        is Result.Success -> SearchResultUiState.Success(
                                            topics = result.data.topics,
                                            newsResources = result.data.newsResources,
                                        )

                                        // 加载中，返回SearchResultUiState.Loading。
                                        is Result.Loading -> SearchResultUiState.Loading
                                        // 失败，返回SearchResultUiState.LoadFailed。
                                        is Result.Error -> SearchResultUiState.LoadFailed
                                    }
                                }
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SearchResultUiState.Loading,
            )

    // 最近搜索查询列表的UiState，默认加载中。
    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        recentSearchQueriesUseCase()
            // Flow<List<RecentSearchQuery>>全部转为成功
            .map(RecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RecentSearchQueriesUiState.Loading,
            )

    // 搜索查询改变，搜索输入框内容改变触发。
    fun onSearchQueryChanged(query: String) {
        // 保存更改，使其触发新的查询。
        savedStateHandle[SEARCH_QUERY] = query
    }

    /**
     * Called when the search action is explicitly triggered by the user. For example, when the
     * search icon is tapped in the IME or when the enter key is pressed in the search text field.
     * 当用户显式触发搜索操作时调用。例如，在IME中点击搜索图标或在搜索文本字段中按enter键时。
     *
     * The search results are displayed on the fly as the user types, but to explicitly save the
     * search query in the search text field, defining this method.
     * 搜索结果在用户键入时动态显示，但要显式地将搜索查询保存在搜索文本字段中，定义此方法。
     */
    // 搜索触发的，以下操作会触发此通知。
    // -点击软件盘搜索按钮
    // -输入回车事件
    // -最近搜索Item点击
    // -搜索成功-Topics（主题）列表-Item点击
    // -搜索成功-Updates（新闻摘要）列表-Item点击
    fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            // 保存搜索结果
            recentSearchRepository.insertOrReplaceRecentSearch(searchQuery = query)
        }
        // 分析辅助-记录
        analyticsHelper.logEventSearchTriggered(query = query)
    }

    // 清除最近搜索列表
    fun clearRecentSearches() {
        viewModelScope.launch {
            // 清除
            recentSearchRepository.clearRecentSearches()
        }
    }
}

private fun AnalyticsHelper.logEventSearchTriggered(query: String) =
    logEvent(
        event = AnalyticsEvent(
            type = SEARCH_QUERY,
            extras = listOf(element = Param(key = SEARCH_QUERY, value = query)),
        ),
    )

/** Minimum length where search query is considered as [SearchResultUiState.EmptyQuery] */
// 搜索查询被认为是[SearchResultUiState.EmptyQuery]的最小长度
private const val SEARCH_QUERY_MIN_LENGTH = 2

/** Minimum number of the fts table's entity count where it's considered as search is not ready */
// fts表中被认为是搜索未准备好的实体计数的最小数目
private const val SEARCH_MIN_FTS_ENTITY_COUNT = 1
private const val SEARCH_QUERY = "searchQuery"
