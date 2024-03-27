/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.feature.topic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.repository.NewsResourceQuery
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.result.Result
import com.google.samples.apps.nowinandroid.core.result.asResult
import com.google.samples.apps.nowinandroid.feature.topic.navigation.TopicArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
// Topic（主题）屏-ViewModel
class TopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userDataRepository: UserDataRepository,
    topicsRepository: TopicsRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
) : ViewModel() {

    // 主题参数（topicId），必传，不传会崩溃。
    private val topicArgs: TopicArgs = TopicArgs(savedStateHandle)

    val topicId = topicArgs.topicId

    // Topic（主题）屏-UiState
    val topicUiState: StateFlow<TopicUiState> = topicUiState(
        topicId = topicArgs.topicId,
        userDataRepository = userDataRepository,
        topicsRepository = topicsRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TopicUiState.Loading,
        )

    // 新闻-UiState
    val newsUiState: StateFlow<NewsUiState> = newsUiState(
        topicId = topicArgs.topicId,
        userDataRepository = userDataRepository,
        userNewsResourceRepository = userNewsResourceRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NewsUiState.Loading,
        )

    // 关注主题切换
    fun followTopicToggle(followed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(topicArgs.topicId, followed)
        }
    }

    // 关注新闻书签切换
    fun bookmarkNews(newsResourceId: String, bookmarked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceBookmarked(newsResourceId, bookmarked)
        }
    }

    // 设置新闻资源已浏览
    fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(newsResourceId, viewed)
        }
    }
}

// TopicUiState生成
private fun topicUiState(
    topicId: String,
    userDataRepository: UserDataRepository,
    topicsRepository: TopicsRepository,
): Flow<TopicUiState> {
    // Observe the followed topics, as they could change over time.
    // 观察下面的主题，因为它们可能会随着时间的推移而改变。
    // -获取全部已经关注的id列表的Flow。
    val followedTopicIds: Flow<Set<String>> =
        userDataRepository.userData
            .map { it.followedTopics }

    // Observe topic information
    // 观察主题信息
    // -获取此Topic主题信息的Flow。内部无是否关注。
    val topicStream: Flow<Topic> = topicsRepository.getTopic(
        id = topicId,
    )

    return combine(
        followedTopicIds,
        topicStream,
        // 转对
        ::Pair,
    )
        // 转为Result类，包含了：开始为Loading、异常为Error、成功为Success。
        .asResult()
        .map { followedTopicToTopicResult ->
            when (followedTopicToTopicResult) {
                // 成功
                is Result.Success -> {
                    // Pair对，分解。
                    val (followedTopics, topic) = followedTopicToTopicResult.data
                    // 转成功，FollowableTopic。
                    TopicUiState.Success(
                        followableTopic = FollowableTopic(
                            topic = topic,
                            isFollowed = topicId in followedTopics,
                        ),
                    )
                }

                // 加载中
                is Result.Loading -> TopicUiState.Loading
                // 失败
                is Result.Error -> TopicUiState.Error
            }
        }
}

// NewsUiState生成
private fun newsUiState(
    topicId: String,
    userNewsResourceRepository: UserNewsResourceRepository,
    userDataRepository: UserDataRepository,
): Flow<NewsUiState> {
    // Observe news
    // 观察新闻
    // -获取到此topicId主题Id的所有新闻资源列表的Flow。
    val newsStream: Flow<List<UserNewsResource>> = userNewsResourceRepository.observeAll(
        NewsResourceQuery(filterTopicIds = setOf(element = topicId)),
    )

    // Observe bookmarks
    // 观察书签
    // -获取已加入书签的新闻资源Id列表的Flow
    val bookmark: Flow<Set<String>> = userDataRepository.userData
        .map { it.bookmarkedNewsResources }

    return combine(newsStream, bookmark, ::Pair)
        .asResult()
        .map { newsToBookmarksResult ->
            when (newsToBookmarksResult) {
                // 成功
                is Result.Success -> NewsUiState.Success(newsToBookmarksResult.data.first)
                // 加载中
                is Result.Loading -> NewsUiState.Loading
                // 失败
                is Result.Error -> NewsUiState.Error
            }
        }
}

sealed interface TopicUiState {
    // 成功，返回FollowableTopic（带有是否关注的Topic）。
    data class Success(val followableTopic: FollowableTopic) : TopicUiState
    data object Error : TopicUiState
    data object Loading : TopicUiState
}

sealed interface NewsUiState {
    // 成功，返回带有新闻列表的UserNewsResource。
    data class Success(val news: List<UserNewsResource>) : NewsUiState
    data object Error : NewsUiState
    data object Loading : NewsUiState
}
