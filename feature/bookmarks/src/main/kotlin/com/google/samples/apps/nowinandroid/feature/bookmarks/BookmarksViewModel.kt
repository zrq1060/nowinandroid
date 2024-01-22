/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.feature.bookmarks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
// ForYou（为你）屏-ViewModel
class BookmarksViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
) : ViewModel() {

    // 是否展示撤销书签提示
    var shouldDisplayUndoBookmark by mutableStateOf(false)
    // 最后一个移除的书签Id
    private var lastRemovedBookmarkId: String? = null

    // 新闻提要列表-UiState
    val feedUiState: StateFlow<NewsFeedUiState> =
        // 获取所有-已经加入书签的-新闻摘要列表
        userNewsResourceRepository.observeAllBookmarked()
            // List<UserNewsResource>列表转一个NewsFeedUiState.Success
            .map<List<UserNewsResource>, NewsFeedUiState>(NewsFeedUiState::Success)
            // 开始为加载中
            .onStart { emit(Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Loading,
            )

    // 移除保存的新闻资源
    fun removeFromSavedResources(newsResourceId: String) {
        viewModelScope.launch {
            // 展示撤销提示
            shouldDisplayUndoBookmark = true
            // 记录最后移除的Id，好方便撤回移除。
            lastRemovedBookmarkId = newsResourceId
            // 移除操作（底层DataStore）
            userDataRepository.updateNewsResourceBookmark(newsResourceId, false)
        }
    }

    // 设置新闻资源是否已浏览
    fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(newsResourceId, viewed)
        }
    }

    // 撤销书签移除
    fun undoBookmarkRemoval() {
        viewModelScope.launch {
            lastRemovedBookmarkId?.let {
                // 恢复移除
                userDataRepository.updateNewsResourceBookmark(it, true)
            }
        }
        // 清除撤销状态，防止再次展示是否撤销提示。
        clearUndoState()
    }

    // 清除撤销状态，防止再次展示是否撤销提示。
    fun clearUndoState() {
        shouldDisplayUndoBookmark = false
        lastRemovedBookmarkId = null
    }
}
