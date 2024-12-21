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

package com.google.samples.apps.nowinandroid.core.ui

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.analytics.LocalAnalyticsHelper
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource

/**
 * An extension on [LazyListScope] defining a feed with news resources.
 * Depending on the [feedState], this might emit no items.
 * LazyListScope的扩展，定义了一个带有新闻资源的提要。根据feedState的不同，这可能不会产生任何项。
 */
// 新闻摘要-item列表，NewsFeedUiState.Success展示新闻资源列表，处理了点击item打开网页；NewsFeedUiState.Loading什么都不展示。用于交错网格使用。
fun LazyStaggeredGridScope.newsFeed(
    feedState: NewsFeedUiState,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onExpandedCardClick: () -> Unit = {},
) {
    when (feedState) {
        NewsFeedUiState.Loading -> Unit
        is NewsFeedUiState.Success -> {
            // 新闻摘要列表
            items(
                items = feedState.feed,
                key = { it.id },
                contentType = { "newsFeedItem" },
            ) { userNewsResource ->
                val context = LocalContext.current
                // 分析帮助类
                val analyticsHelper = LocalAnalyticsHelper.current
                // 背景颜色
                val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

                // 新闻item
                NewsResourceCardExpanded(
                    userNewsResource = userNewsResource,
                    isBookmarked = userNewsResource.isSaved,
                    // 新闻Item点击
                    onClick = {
                        // 通知展开卡片点击
                        onExpandedCardClick()
                        // 分析帮助类，传递id信息。
                        analyticsHelper.logNewsResourceOpened(
                            newsResourceId = userNewsResource.id,
                        )
                        // 启动打开网页
                        launchCustomChromeTab(context, Uri.parse(userNewsResource.url), backgroundColor)

                        // 通知新闻资源可见
                        onNewsResourceViewed(userNewsResource.id)
                    },
                    // 是否已浏览
                    hasBeenViewed = userNewsResource.hasBeenViewed,
                    onToggleBookmark = {
                        // 切换书签
                        onNewsResourcesCheckedChanged(
                            userNewsResource.id,
                            !userNewsResource.isSaved,
                        )
                    },
                    // 主题（item最下面水平布局）点击
                    onTopicClick = onTopicClick,
                    // 修饰：内边距水平为8，宽占满。
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItem(),
                )
            }
        }
    }
}

// 启动打开网页
fun launchCustomChromeTab(context: Context, uri: Uri, @ColorInt toolbarColor: Int) {
    // 设置TabBar颜色
    val customTabBarColor = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor).build()
    // 设置颜色
    val customTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(customTabBarColor)
        .build()
    // 打开网页
    customTabsIntent.launchUrl(context, uri)
}

/**
 * A sealed hierarchy describing the state of the feed of news resources.
 * 描述新闻资源提要状态的密封层次结构。
 */
// 新闻提要-UiState
sealed interface NewsFeedUiState {
    /**
     * The feed is still loading.
     * feed仍在加载中。
     */
    data object Loading : NewsFeedUiState

    /**
     * The feed is loaded with the given list of news resources.
     * 提要加载了给定的新闻资源列表。
     */
    data class Success(
        /**
         * The list of news resources contained in this feed.
         * 此提要中包含的新闻资源列表。
         */
        val feed: List<UserNewsResource>,
    ) : NewsFeedUiState
}

@Preview
@Composable
// 加载中，不显示任何布局。
private fun NewsFeedLoadingPreview() {
    NiaTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            newsFeed(
                feedState = NewsFeedUiState.Loading,
                onNewsResourcesCheckedChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}

// 显示内容列表，手机设备
@Preview
// 显示内容列表，平板设备
@Preview(device = Devices.TABLET)
@Composable
private fun NewsFeedContentPreview(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    // 参数，由UserNewsResourcePreviewParameterProvider提供。
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            newsFeed(
                feedState = NewsFeedUiState.Success(userNewsResources),
                onNewsResourcesCheckedChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}
