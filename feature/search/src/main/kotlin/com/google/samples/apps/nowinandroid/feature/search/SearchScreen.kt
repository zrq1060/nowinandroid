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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.DevicePreviews
import com.google.samples.apps.nowinandroid.core.ui.InterestsItem
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState.Success
import com.google.samples.apps.nowinandroid.core.ui.R.string
import com.google.samples.apps.nowinandroid.core.ui.TrackScreenViewEvent
import com.google.samples.apps.nowinandroid.core.ui.newsFeed
import com.google.samples.apps.nowinandroid.feature.search.R as searchR

@Composable
// Search（搜索）屏-路由，有ViewModel。
internal fun SearchRoute(
    // 返回点击
    onBackClick: () -> Unit,
    // Interests（兴趣）点击，在空的搜索结果上展示此按钮。
    onInterestsClick: () -> Unit,
    // 主题点击，在有数据的搜索结果上展示。
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    // 搜索的ViewModel
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
    val recentSearchQueriesUiState by searchViewModel.recentSearchQueriesUiState.collectAsStateWithLifecycle()
    val searchResultUiState by searchViewModel.searchResultUiState.collectAsStateWithLifecycle()
    val searchQuery by searchViewModel.searchQuery.collectAsStateWithLifecycle()
    SearchScreen(
        modifier = modifier,
        searchQuery = searchQuery,
        recentSearchesUiState = recentSearchQueriesUiState,
        searchResultUiState = searchResultUiState,
        onSearchQueryChanged = searchViewModel::onSearchQueryChanged,
        // 搜索触发的
        onSearchTriggered = searchViewModel::onSearchTriggered,
        onClearRecentSearches = searchViewModel::clearRecentSearches,
        // 新闻资源关注改变
        onNewsResourcesCheckedChanged = searchViewModel::setNewsResourceBookmarked,
        // 新闻资源已浏览
        onNewsResourceViewed = { searchViewModel.setNewsResourceViewed(it, true) },
        onFollowButtonClick = searchViewModel::followTopic,
        onBackClick = onBackClick,
        onInterestsClick = onInterestsClick,
        // 主题点击
        onTopicClick = onTopicClick,
    )
}

@Composable
// Search（搜索）屏-UI，无ViewModel。
internal fun SearchScreen(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    recentSearchesUiState: RecentSearchQueriesUiState = RecentSearchQueriesUiState.Loading,
    searchResultUiState: SearchResultUiState = SearchResultUiState.Loading,
    onSearchQueryChanged: (String) -> Unit = {},
    onSearchTriggered: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit = { _, _ -> },
    onNewsResourceViewed: (String) -> Unit = {},
    onFollowButtonClick: (String, Boolean) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {},
    onInterestsClick: () -> Unit = {},
    onTopicClick: (String) -> Unit = {},
) {
    // 记录事件
    TrackScreenViewEvent(screenName = "Search")
    // 列容器
    Column(modifier = modifier) {
        // 间隔
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        // 搜索标题栏
        SearchToolbar(
            onBackClick = onBackClick,
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchTriggered = onSearchTriggered,
            searchQuery = searchQuery,
        )
        when (searchResultUiState) {
            // 搜索结果为加载中、加载失败，不展示。
            SearchResultUiState.Loading,
            SearchResultUiState.LoadFailed,
            -> Unit

            // 搜索结果为没准备好，展示等待提示。
            SearchResultUiState.SearchNotReady -> SearchNotReadyBody()
            // 搜索结果为空查询（长度不够），只展示最近搜索查询列表。
            SearchResultUiState.EmptyQuery,
            -> {
                if (recentSearchesUiState is RecentSearchQueriesUiState.Success) {
                    // 最近搜索查询为成功，则展示此最近搜索查询列表。
                    RecentSearchesBody(
                        // 清除最近搜索列表
                        onClearRecentSearches = onClearRecentSearches,
                        // 最近搜索点击
                        onRecentSearchClicked = {
                            // 通知搜索查询改变
                            onSearchQueryChanged(it)
                            // 通知搜索触发的
                            onSearchTriggered(it)
                        },
                        // 最近搜索查询列表
                        recentSearchQueries = recentSearchesUiState.recentQueries.map { it.query },
                    )
                }
            }

            // 搜索结果为成功，判断是否为空，空展示无发现提示+最近搜索查询列表，非空展示Topics+Updates。
            is SearchResultUiState.Success -> {
                if (searchResultUiState.isEmpty()) {
                    // 搜索结果为空，展示提示无发现布局+最近搜索查询列表。
                    // -无发现提示布局
                    EmptySearchResultBody(
                        searchQuery = searchQuery,
                        onInterestsClick = onInterestsClick,
                    )
                    // -最近搜索查询列表
                    if (recentSearchesUiState is RecentSearchQueriesUiState.Success) {
                        RecentSearchesBody(
                            onClearRecentSearches = onClearRecentSearches,
                            onRecentSearchClicked = {
                                onSearchQueryChanged(it)
                                onSearchTriggered(it)
                            },
                            recentSearchQueries = recentSearchesUiState.recentQueries.map { it.query },
                        )
                    }
                } else {
                    // 搜索结果不为空，展示搜索结果（Topics+Updates）。
                    SearchResultBody(
                        searchQuery = searchQuery,
                        topics = searchResultUiState.topics,
                        newsResources = searchResultUiState.newsResources,
                        onSearchTriggered = onSearchTriggered,
                        onTopicClick = onTopicClick,
                        onNewsResourcesCheckedChanged = onNewsResourcesCheckedChanged,
                        onNewsResourceViewed = onNewsResourceViewed,
                        onFollowButtonClick = onFollowButtonClick,
                    )
                }
            }
        }
        // 间隔-item一项，解决无网络Snackbar的展示。
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
    }
}

@Composable
// 搜索结果为空，无发现提示布局。
fun EmptySearchResultBody(
    searchQuery: String,
    onInterestsClick: () -> Unit,
) {
    // 列容器
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 48.dp),
    ) {
        // 提示无发现
        val message = stringResource(id = searchR.string.feature_search_result_not_found, searchQuery)
        val start = message.indexOf(searchQuery)
        Text(
            text = AnnotatedString(
                text = message,
                // Span样式，查询内容的开始位置到结束位置字体加粗。
                spanStyles = listOf(
                    AnnotatedString.Range(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        start = start,
                        end = start + searchQuery.length,
                    ),
                ),
            ),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp),
        )
        // 提示尝试搜索其它或者到Interests来浏览主题
        val interests = stringResource(id = searchR.string.feature_search_interests)
        val tryAnotherSearchString = buildAnnotatedString {
            // 【Try another search or explorer 】
            append(stringResource(id = searchR.string.feature_search_try_another_search))
            // 加空格，上面的内容内带不生效。
            append(" ")
            // 样式，加粗+下划线。
            withStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                ),
            ) {
                pushStringAnnotation(tag = interests, annotation = interests)
                append(interests)
            }
            // 加空格，下面的内容内带不生效。
            append(" ")
            // 【 to browse topics】
            append(stringResource(id = searchR.string.feature_search_to_browse_topics))
        }
        // -可点击的文本
        ClickableText(
            // 组合AnnotatedString文本
            text = tryAnotherSearchString,
            // 样式，大body字体，居中。
            style = MaterialTheme.typography.bodyLarge.merge(
                TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                ),
            ),
            modifier = Modifier
                .padding(start = 36.dp, end = 36.dp, bottom = 24.dp)
                .clickable {},
        ) { offset ->
            // 当用户单击文本时执行的回调。这个回调用被点击的字符的偏移量来调用。
            tryAnotherSearchString.getStringAnnotations(start = offset, end = offset)
                .firstOrNull()
                // 通知主题点击
                ?.let { onInterestsClick() }
        }
    }
}

@Composable
// 搜索结果为没准备好，展示等待提示。
private fun SearchNotReadyBody() {
    // 列容器
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 48.dp),
    ) {
        // 等待提示
        Text(
            text = stringResource(id = searchR.string.feature_search_not_ready),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp),
        )
    }
}

@Composable
// 搜索结果不为空，展示搜索结果（Topics+Updates）。
private fun SearchResultBody(
    searchQuery: String,
    topics: List<FollowableTopic>,
    newsResources: List<UserNewsResource>,
    onSearchTriggered: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onFollowButtonClick: (String, Boolean) -> Unit,
) {
    val state = rememberLazyStaggeredGridState()
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        // 垂直流式容器
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 24.dp,
            modifier = Modifier
                .fillMaxSize()
                .testTag("search:newsResources"),
            state = state,
        ) {
            // Topics
            if (topics.isNotEmpty()) {
                // Topics-标题，全行。
                item(
                    span = StaggeredGridItemSpan.FullLine,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            // 字体加粗
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(id = searchR.string.feature_search_topics))
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
                // Topics-列表内容，全行。
                topics.forEach { followableTopic ->
                    val topicId = followableTopic.topic.id
                    item(
                        // Append a prefix to distinguish a key for news resources
                        // 附加前缀以区分新闻资源的关键字
                        key = "topic-$topicId",
                        span = StaggeredGridItemSpan.FullLine,
                    ) {
                        // Item布局
                        InterestsItem(
                            name = followableTopic.topic.name,
                            following = followableTopic.isFollowed,
                            description = followableTopic.topic.shortDescription,
                            topicImageUrl = followableTopic.topic.imageUrl,
                            onClick = {
                                // Pass the current search query to ViewModel to save it as recent searches
                                // 将当前搜索查询传递给ViewModel以将其保存为最近的搜索
                                onSearchTriggered(searchQuery)
                                // 通知主题点击
                                onTopicClick(topicId)
                            },
                            onFollowButtonClick = { onFollowButtonClick(topicId, it) },
                        )
                    }
                }
            }

            // Updates
            if (newsResources.isNotEmpty()) {
                // Updates-标题，全行。
                item(
                    span = StaggeredGridItemSpan.FullLine,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            // 字体加粗
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(id = searchR.string.feature_search_updates))
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                // Updates-列表内容，非全行。
                newsFeed(
                    // 直接成功的状态
                    feedState = Success(feed = newsResources),
                    onNewsResourcesCheckedChanged = onNewsResourcesCheckedChanged,
                    onNewsResourceViewed = onNewsResourceViewed,
                    onTopicClick = onTopicClick,
                    onExpandedCardClick = {
                        // 卡片点击，通知搜索
                        onSearchTriggered(searchQuery)
                    },
                )
            }
        }
        val itemsAvailable = topics.size + newsResources.size
        val scrollbarState = state.scrollbarState(
            itemsAvailable = itemsAvailable,
        )
        // 可拖动滚动条（右侧）
        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
}

@Composable
// 最近搜索查询列表
private fun RecentSearchesBody(
    recentSearchQueries: List<String>,
    onClearRecentSearches: () -> Unit,
    onRecentSearchClicked: (String) -> Unit,
) {
    // 列容器
    Column {
        // Recent searches+右侧删除按钮，行容器，水平两端对其。
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Recent searches-标题
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        // 字体加粗
                        append(stringResource(id = searchR.string.feature_search_recent_searches))
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            // Recent searches-右侧删除按钮
            if (recentSearchQueries.isNotEmpty()) {
                IconButton(
                    onClick = {
                        // 点击清空最近搜索
                        onClearRecentSearches()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Icon(
                        imageVector = NiaIcons.Close,
                        contentDescription = stringResource(
                            id = searchR.string.feature_search_clear_recent_searches_content_desc,
                        ),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        // Recent searches-列表内容
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(recentSearchQueries) { recentSearch ->
                Text(
                    text = recentSearch,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        // Item点击，通知最近搜索点击。
                        .clickable { onRecentSearchClicked(recentSearch) }
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
// 搜索标题栏
private fun SearchToolbar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 行容器
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        // 左边返回按钮，点击通知onBackClick。
        IconButton(onClick = { onBackClick() }) {
            Icon(
                imageVector = NiaIcons.ArrowBack,
                contentDescription = stringResource(
                    id = string.core_ui_back,
                ),
            )
        }
        // 搜索文本框
        SearchTextField(
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchTriggered = onSearchTriggered,
            searchQuery = searchQuery,
        )
    }
}

@Composable
// 搜索文本框
private fun SearchTextField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
) {
    // 焦点请求者
    val focusRequester = remember { FocusRequester() }
    // 键盘控制者
    val keyboardController = LocalSoftwareKeyboardController.current

    // 明确触发搜索，通知隐藏键盘，通知搜索触发。
    val onSearchExplicitlyTriggered = {
        keyboardController?.hide()
        onSearchTriggered(searchQuery)
    }

    // 文本框
    TextField(
        // 颜色，指示器颜色为透明。
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        // 头Icon
        leadingIcon = {
            Icon(
                imageVector = NiaIcons.Search,
                contentDescription = stringResource(
                    id = searchR.string.feature_search_title,
                ),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        // 尾Icon，有搜索内容才展示，点击清除查询。
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        // 点击，清除查询。
                        onSearchQueryChanged("")
                    },
                ) {
                    Icon(
                        imageVector = NiaIcons.Close,
                        contentDescription = stringResource(
                            id = searchR.string.feature_search_clear_search_text_content_desc,
                        ),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        // 值改变，如果里面没有换行，则通知搜索查询改变。
        onValueChange = {
            if ("\n" !in it) onSearchQueryChanged(it)
        },
        // 修饰：最大宽、内边距、焦点请求者、点击回车事件（兼容外设键盘）则通知明确触发搜索。
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    onSearchExplicitlyTriggered()
                    true
                } else {
                    false
                }
            }
            .testTag("searchTextField"),
        // shape：圆角矩形
        shape = RoundedCornerShape(32.dp),
        // 值
        value = searchQuery,
        // 键盘选项：搜索键盘
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        // 键盘点击，点击搜索按钮，通知明确触发搜索。
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchExplicitlyTriggered()
            },
        ),
        // 最大行为1
        maxLines = 1,
        // 单行
        singleLine = true,
    )
    // 请求焦点
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
// 预览-搜索标题栏
private fun SearchToolbarPreview() {
    NiaTheme {
        SearchToolbar(
            searchQuery = "",
            onBackClick = {},
            onSearchQueryChanged = {},
            onSearchTriggered = {},
        )
    }
}

@Preview
@Composable
// 预览-搜索结果为空，展示无发现提示布局。
private fun EmptySearchResultColumnPreview() {
    NiaTheme {
        EmptySearchResultBody(
            onInterestsClick = {},
            searchQuery = "C++",
        )
    }
}

@Preview
@Composable
// 预览-最近搜索查询列表，3条数据。
private fun RecentSearchesBodyPreview() {
    NiaTheme {
        RecentSearchesBody(
            onClearRecentSearches = {},
            onRecentSearchClicked = {},
            recentSearchQueries = listOf("kotlin", "jetpack compose", "testing"),
        )
    }
}

@Preview
@Composable
// 预览-搜索结果为没准备好，展示等待提示。
private fun SearchNotReadyBodyPreview() {
    NiaTheme {
        SearchNotReadyBody()
    }
}

@DevicePreviews
@Composable
// Search（搜索）屏-UI预览
// 在手机-竖屏、手机-横屏、折叠屏、平板上，展示成功的Search（搜索）屏（Topics、Updates都是3条数据）。
private fun SearchScreenPreview(
    @PreviewParameter(SearchUiStatePreviewParameterProvider::class)
    searchResultUiState: SearchResultUiState,
) {
    NiaTheme {
        SearchScreen(searchResultUiState = searchResultUiState)
    }
}
