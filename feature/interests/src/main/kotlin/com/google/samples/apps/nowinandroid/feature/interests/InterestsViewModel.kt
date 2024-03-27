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

package com.google.samples.apps.nowinandroid.feature.interests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.domain.GetFollowableTopicsUseCase
import com.google.samples.apps.nowinandroid.core.domain.TopicSortField
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.feature.interests.navigation.TOPIC_ID_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
// Interests（兴趣）屏-ViewModel
class InterestsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val userDataRepository: UserDataRepository,
    // 获取带有是否关注的FollowableTopic列表的用例
    getFollowableTopics: GetFollowableTopicsUseCase,
) : ViewModel() {

    val selectedTopicId: StateFlow<String?> = savedStateHandle.getStateFlow(TOPIC_ID_ARG, null)

    // Interests（兴趣）屏-UiState，默认加载中状态。
    val uiState: StateFlow<InterestsUiState> = combine(
        selectedTopicId,
        // 获取FollowableTopic（带有是否关注的）列表，并按照名称排序
        getFollowableTopics(sortBy = TopicSortField.NAME),
        InterestsUiState::Interests,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InterestsUiState.Loading,
    )

    // 设置-关注/取消关注-主题
    fun followTopic(followedTopicId: String, followed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(followedTopicId, followed)
        }
    }

    fun onTopicClick(topicId: String?) {
        savedStateHandle[TOPIC_ID_ARG] = topicId
    }
}

// Interests（兴趣）屏-UiState
sealed interface InterestsUiState {
    // 加载中
    data object Loading : InterestsUiState

    // 成功-有数据
    data class Interests(
        val selectedTopicId: String?,
        val topics: List<FollowableTopic>,
    ) : InterestsUiState

    // 成功-无数据
    data object Empty : InterestsUiState
}
