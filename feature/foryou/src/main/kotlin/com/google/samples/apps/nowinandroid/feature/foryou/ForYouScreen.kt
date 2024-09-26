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

package com.google.samples.apps.nowinandroid.feature.foryou

import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus.Denied
import com.google.accompanist.permissions.rememberPermissionState
import com.google.samples.apps.nowinandroid.core.designsystem.component.DynamicAsyncImage
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaIconToggleButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaOverlayLoadingWheel
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DecorativeScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.DevicePreviews
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import com.google.samples.apps.nowinandroid.core.ui.TrackScreenViewEvent
import com.google.samples.apps.nowinandroid.core.ui.TrackScrollJank
import com.google.samples.apps.nowinandroid.core.ui.UserNewsResourcePreviewParameterProvider
import com.google.samples.apps.nowinandroid.core.ui.launchCustomChromeTab
import com.google.samples.apps.nowinandroid.core.ui.newsFeed

@Composable
internal fun ForYouScreen(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForYouViewModel = hiltViewModel(),
) {
    val onboardingUiState by viewModel.onboardingUiState.collectAsStateWithLifecycle()
    val feedState by viewModel.feedState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val deepLinkedUserNewsResource by viewModel.deepLinkedNewsResource.collectAsStateWithLifecycle()

    ForYouScreen(
        // 是否同步数据同步中，SyncManager实现。
        isSyncing = isSyncing,
        onboardingUiState = onboardingUiState,
        feedState = feedState,
        deepLinkedUserNewsResource = deepLinkedUserNewsResource,
        onTopicCheckedChanged = viewModel::updateTopicSelection,
        onDeepLinkOpened = viewModel::onDeepLinkOpened,
        onTopicClick = onTopicClick,
        saveFollowedTopics = viewModel::dismissOnboarding,
        onNewsResourcesCheckedChanged = viewModel::updateNewsResourceSaved,
        onNewsResourceViewed = { viewModel.setNewsResourceViewed(it, true) },
        modifier = modifier,
    )
}

// ForYou（为你）屏-UI，无ViewModel。
@Composable
internal fun ForYouScreen(
    isSyncing: Boolean,
    onboardingUiState: OnboardingUiState,
    feedState: NewsFeedUiState,
    deepLinkedUserNewsResource: UserNewsResource?,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    onDeepLinkOpened: (String) -> Unit,
    saveFollowedTopics: () -> Unit,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 是否引导数据加载中
    val isOnboardingLoading = onboardingUiState is OnboardingUiState.Loading
    // 是否新闻提要加载中
    val isFeedLoading = feedState is NewsFeedUiState.Loading

    // This code should be called when the UI is ready for use and relates to Time To Full Display.
    // 当UI准备好使用时，应该调用此代码，并与完全显示时间相关。
    // ReportDrawnWhen：向调用Activity.reportFullyDrawn之前必须满足的条件添加谓词。
    // 当所有数据都加载完成，向系统通知当前 Activity 已经完全绘制，以协助系统进行性能优化和用户体验的改进。
    ReportDrawnWhen { !isSyncing && !isOnboardingLoading && !isFeedLoading }

    // 可用的item总数
    val itemsAvailable = feedItemsSize(feedState, onboardingUiState)

    // rememberLazyStaggeredGridState：
    // -在重组时用不同的参数调用这个函数不会重新创建或改变状态。
    // -使用LazyStaggeredGridState.scrollToItem或LazyStaggeredGridState.animateScrollToItem来调整位置。
    // -参数:
    // --initialFirstVisibleItemIndex - LazyStaggeredGridState.firstVisibleItemIndex的初始位置
    // --initialFirstVisibleItemScrollOffset - LazyStaggeredGridState.firstVisibleItemScrollOffset的初始值
    // --initialFirstVisibleItemScrollOffset - LazyStaggeredGridState.firstVisibleItemScrollOffset的初始值
    // -返回:
    // --使用给定的参数创建和记忆LazyStaggeredGridState。
    val state = rememberLazyStaggeredGridState()
    // scrollbarState：记住由LazyStaggeredGridState的变化驱动的ScrollbarState
    val scrollbarState = state.scrollbarState(
        itemsAvailable = itemsAvailable,
    )
    // 记录是否滚动中
    TrackScrollJank(scrollableState = state, stateName = "forYou:feed")

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        // 垂直网格流布局，方便适配横屏和平板屏。
        LazyVerticalStaggeredGrid(
            // Adaptive：
            // -定义一个具有尽可能多的行或列的网格，条件是每个单元格至少具有minSize的空间，并且所有额外的空间均匀分布。
            // -例如，对于垂直LazyVerticalStaggeredGrid Adaptive(20dp)将意味着将有尽可能多的列，每列至少20dp，所有列将具有相同的宽度。如果屏幕宽度为88dp，那么将有4列，每列22dp。
            columns = StaggeredGridCells.Adaptive(300.dp),
            // 内容Padding
            contentPadding = PaddingValues(16.dp),
            // spacedBy：
            // -放置子元素，使每个相邻的两个元素在主轴上以固定的空间距离间隔。这个间隔将从孩子们可以占用的可用空间中减去。空间可以是负的，在这种情况下子元素会重叠。
            // -若要在水平或垂直方向更改间隔子元素的对齐方式，请使用带对齐参数的spacedBy重载。
            // 水平排版：间距16。
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            // 垂直间距（可为负数）：24
            verticalItemSpacing = 24.dp,
            modifier = Modifier
                .testTag("forYou:feed"),
            // LazyStaggeredGridState
            state = state,
        ) {
            // 用户引导-item一项，OnboardingUiState.Shown的时候展示（引导提示+水平主题选择列表+Done按钮），其它状态不展示。
            onboarding(
                onboardingUiState = onboardingUiState,
                // 主题改变通知
                onTopicCheckedChanged = onTopicCheckedChanged,
                // 保存关注的主题通知，点Done按钮的时候通知。
                saveFollowedTopics = saveFollowedTopics,
                // Custom LayoutModifier to remove the enforced parent 16.dp contentPadding
                // from the LazyVerticalGrid and enable edge-to-edge scrolling for this section
                // 自定义LayoutModifier，从LazyVerticalGrid中删除强制的父级16dp内容填充，并启用此部分的边缘到边缘滚动
                interestsItemModifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        constraints.copy(
                            // 去掉上面的设置的contentPadding = PaddingValues(16.dp)
                            maxWidth = constraints.maxWidth + 32.dp.roundToPx(),
                        ),
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                },
            )

            // 新闻摘要-item列表，NewsFeedUiState.Success展示新闻资源列表，处理了点击item打开网页；NewsFeedUiState.Loading什么都不展示
            newsFeed(
                feedState = feedState,
                // 新闻资源检查的改变通知，点击切换书签时通知。
                onNewsResourcesCheckedChanged = onNewsResourcesCheckedChanged,
                // 新闻资源已预览通知，点击Item时通知。
                onNewsResourceViewed = onNewsResourceViewed,
                // 主题点击通知，点击底部主题Item时通知。
                onTopicClick = onTopicClick,
            )

            // 间隔-item一项，解决无网络Snackbar的展示。
            item(span = StaggeredGridItemSpan.FullLine, contentType = "bottomSpacing") {
                Column {
                    // 间隔8
                    Spacer(modifier = Modifier.height(8.dp))
                    // Add space for the content to clear the "offline" snackbar.
                    // 为内容添加空间，以清除“离线”snackbar。
                    // TODO: Check that the Scaffold handles this correctly in NiaApp
                    // TODO: 检查Scaffold在NiaApp中是否正确处理了这个问题
                    // if (isOffline) Spacer(modifier = Modifier.height(48.dp))
                    // 间隔底部的高
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
        }
        // Loading的显示隐藏动画
        AnimatedVisibility(
            // 是否显示，是否同步数据同步中（SyncManager实现）、是否新闻提要加载中、是否引导数据加载中，有一个在加载中则显示Loading。
            visible = isSyncing || isFeedLoading || isOnboardingLoading,
            // 进入动画，垂直滑进+淡入。
            enter = slideInVertically(
                // 初始化，从-自己到自己。
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            // 退出动画，垂直滑出+淡出。
            exit = slideOutVertically(
                // 初始化，从自己到-自己。
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription = stringResource(id = R.string.feature_foryou_loading)
            // Loading容器布局
            Box(
                // 修饰：宽占满，内边距为8。
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                // Loading布局
                NiaOverlayLoadingWheel(
                    // 修饰：居中。
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }

        // 可拖动滚动条（右侧）
        state.DraggableScrollbar(
            // 修饰：高占满，内边距水平为2，右边居中。
            modifier = Modifier
                .fillMaxHeight()
                // 添加填充，使内容不会进入WindowInsets.systemBars空间。
                // 效果是内部拖动条下移了
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            // 状态
            state = scrollbarState,
            // 垂直滚动
            orientation = Orientation.Vertical,
            // TODO 待研究
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
    // 日至打印
    TrackScreenViewEvent(screenName = "ForYou")
    // 通知权限效果
    NotificationPermissionEffect()
    // 深度链接效果，用于恢复之前的深度链接。
    DeepLinkEffect(
        deepLinkedUserNewsResource,
        onDeepLinkOpened,
    )
}

/**
 * An extension on [LazyListScope] defining the onboarding portion of the for you screen.
 * Depending on the [onboardingUiState], this might emit no items.
 * LazyListScope上的扩展，定义了for you屏幕的入职部分。根据[onboardingUiState]，这可能不会发射任何项目。
 *
 */
// 用户引导-item，OnboardingUiState.Shown的时候展示（引导提示+水平主题选择列表+Done按钮），其它状态不展示。
private fun LazyStaggeredGridScope.onboarding(
    onboardingUiState: OnboardingUiState,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    saveFollowedTopics: () -> Unit,
    interestsItemModifier: Modifier = Modifier,
) {
    when (onboardingUiState) {
        // Loading、LoadFailed、NotShown不处理。
        OnboardingUiState.Loading,
        OnboardingUiState.LoadFailed,
        OnboardingUiState.NotShown,
        -> Unit

        // Shown展示，引导提示+水平主题选择列表+Done按钮。
        is OnboardingUiState.Shown -> {
            // FullLine：强制项目占据横轴上的整条线。
            // 全占位Item
            item(span = StaggeredGridItemSpan.FullLine, contentType = "onboarding") {
                Column(modifier = interestsItemModifier) {
                    // 引导-标题
                    Text(
                        // 提示：你对什么感兴趣？
                        text = stringResource(R.string.feature_foryou_onboarding_guidance_title),
                        // 内容居中
                        textAlign = TextAlign.Center,
                        // 修饰：宽占满，内边距为24。
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        // 样式：中等标题
                        style = MaterialTheme.typography.titleMedium,
                    )
                    // 引导-副标题
                    Text(
                        // 提示：您关注的主题的更新将出现在这里。遵循一些事情来开始。
                        text = stringResource(R.string.feature_foryou_onboarding_guidance_subtitle),
                        // 修饰：宽占满，内边距为左右24、上8下无。
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 24.dp, end = 24.dp),
                        // 内容居中
                        textAlign = TextAlign.Center,
                        // 样式：中等body
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    // 水平主题选择列表
                    TopicSelection(
                        onboardingUiState,
                        // 主题改变通知
                        onTopicCheckedChanged,
                        // 列表padding为8
                        Modifier.padding(bottom = 8.dp),
                    )
                    // Done button
                    // Done 按钮，点击保存关注的主题（实际是隐藏引导）
                    Row(
                        // 水平排版：居中
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // 普通按钮（具有填充、无边框），无图标。
                        NiaButton(
                            onClick = saveFollowedTopics,
                            // 主题选中了一个，按钮即可点击。
                            enabled = onboardingUiState.isDismissable,
                            // 修饰：宽占满，内边距为左右24、上下无。
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                // widthIn：根据传入的测量约束，将内容的宽度限制在mindp和maxdp之间。如果传入的约束更严格，则请求的大小将服从传入的约束，并尝试尽可能接近首选的大小。
                                // 最小为364，最大未定义。
                                .widthIn(364.dp)
                                .fillMaxWidth(),
                        ) {
                            // 按钮文本
                            Text(
                                text = stringResource(R.string.feature_foryou_done),
                            )
                        }
                    }
                }
            }
        }
    }
}

// 引导布局-水平主题选择列表
@Composable
private fun TopicSelection(
    onboardingUiState: OnboardingUiState.Shown,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyGridState = rememberLazyGridState()
    val topicSelectionTestTag = "forYou:topicSelection"

    TrackScrollJank(scrollableState = lazyGridState, stateName = topicSelectionTestTag)

    Box(
        // 修饰：宽占满
        modifier = modifier
            .fillMaxWidth(),
    ) {
        // 水平网格布局
        LazyHorizontalGrid(
            state = lazyGridState,
            // 3行
            rows = GridCells.Fixed(3),
            // 水平排版：间距12
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            // 垂直排版：间距12
            verticalArrangement = Arrangement.spacedBy(12.dp),
            // 内边距为24
            contentPadding = PaddingValues(24.dp),
            // 修饰：宽占满
            modifier = Modifier
                // LazyHorizontalGrid has to be constrained in height.
                // However, we can't set a fixed height because the horizontal grid contains
                // vertical text that can be rescaled.
                // When the fontScale is at most 1, we know that the horizontal grid will be at most
                // 240dp tall, so this is an upper bound for when the font scale is at most 1.
                // When the fontScale is greater than 1, the height required by the text inside the
                // horizontal grid will increase by at most the same factor, so 240sp is a valid
                // upper bound for how much space we need in that case.
                // The maximum of these two bounds is therefore a valid upper bound in all cases.
                // LazyHorizontalGrid必须限制高度。
                // 但是，我们不能设置一个固定的高度，因为水平网格包含可以重新缩放的垂直文本。
                // 当fontScale最大为1时，我们知道水平网格的高度最多为240dp，所以这是字体大小最大为1时的上限。
                // 当fontScale大于1时，水平网格内的文本所需的高度将最多增加相同的因子，因此240sp是在这种情况下我们需要多少空间的有效上限。
                // 因此，这两个边界的最大值在所有情况下都是有效的上界。

                // 高度，最小未定义，最大为240dp或者240sp。
                .heightIn(max = max(240.dp, with(LocalDensity.current) { 240.sp.toDp() }))
                .fillMaxWidth()
                .testTag(topicSelectionTestTag),
        ) {
            // 水平列表
            items(
                items = onboardingUiState.topics,
                key = { it.topic.id },
            ) {
                // 单个主题按钮
                SingleTopicButton(
                    name = it.topic.name,
                    topicId = it.topic.id,
                    imageUrl = it.topic.imageUrl,
                    isSelected = it.isFollowed,
                    onClick = onTopicCheckedChanged,
                )
            }
        }
        // 滚动条（下侧）
        lazyGridState.DecorativeScrollbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .align(Alignment.BottomStart),
            state = lazyGridState.scrollbarState(itemsAvailable = onboardingUiState.topics.size),
            orientation = Orientation.Horizontal,
        )
    }
}

@Composable
// 引导布局-水平主题列表-单个主题按钮
private fun SingleTopicButton(
    name: String,
    topicId: String,
    imageUrl: String,
    isSelected: Boolean,
    onClick: (String, Boolean) -> Unit,
) {
    Surface(
        // 装饰：宽312，高最小56。
        modifier = Modifier
            .width(312.dp)
            .heightIn(min = 56.dp),
        // 圆角矩形
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        // 颜色
        color = MaterialTheme.colorScheme.surface,
        // 是否选中
        selected = isSelected,
        // 点击，通知
        onClick = {
            onClick(topicId, !isSelected)
        },
    ) {
        // 行布局
        Row(
            // 垂直对齐：垂直居中
            verticalAlignment = Alignment.CenterVertically,
            // 装饰：内边距左12右8。
            modifier = Modifier.padding(start = 12.dp, end = 8.dp),
        ) {
            // Icon，动态下载图片。
            TopicIcon(
                imageUrl = imageUrl,
            )
            // 名称
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    // 权重为1，即占满。
                    .weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
            )
            // 切换按钮
            NiaIconToggleButton(
                checked = isSelected,
                // 通知点击
                onCheckedChange = { checked -> onClick(topicId, checked) },
                icon = {
                    Icon(
                        imageVector = NiaIcons.Add,
                        contentDescription = name,
                    )
                },
                checkedIcon = {
                    Icon(
                        imageVector = NiaIcons.Check,
                        contentDescription = name,
                    )
                },
            )
        }
    }
}

@Composable
// 引导布局-水平主题列表-单个主题按钮-主题Icon
fun TopicIcon(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    DynamicAsyncImage(
        placeholder = painterResource(R.drawable.feature_foryou_ic_icon_placeholder),
        imageUrl = imageUrl,
        // decorative
        contentDescription = null,
        modifier = modifier
            .padding(10.dp)
            .size(32.dp),
    )
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
// 请求通知权限
private fun NotificationPermissionEffect() {
    // Permission requests should only be made from an Activity Context, which is not present
    // in previews
    // 权限请求应该只从活动上下文发出，它不存在于预览中
    // 检查中，不处理。
    if (LocalInspectionMode.current) return
    // 低版本SDK（android-13），不处理。
    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return
    // 通知权限状态
    val notificationsPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )
    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is Denied && !status.shouldShowRationale) {
            // 状态是拒绝的，并且没有显示理由，则请求权限。
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}

@Composable
// 深度链接效果
private fun DeepLinkEffect(
    userNewsResource: UserNewsResource?,
    onDeepLinkOpened: (String) -> Unit,
) {
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    LaunchedEffect(userNewsResource) {
        // 新闻资源为空，直接返回。
        if (userNewsResource == null) return@LaunchedEffect
        // 未浏览，则直接通知深度链接已打开。
        if (!userNewsResource.hasBeenViewed) onDeepLinkOpened(userNewsResource.id)

        // 打开网页
        launchCustomChromeTab(
            context = context,
            uri = Uri.parse(userNewsResource.url),
            toolbarColor = backgroundColor,
        )
    }
}

// 新闻提要列表的数量
private fun feedItemsSize(
    feedState: NewsFeedUiState,
    onboardingUiState: OnboardingUiState,
): Int {
    // 新闻提要数量，加载中为0，加载成功为真正的列表数量。
    val feedSize = when (feedState) {
        NewsFeedUiState.Loading -> 0
        is NewsFeedUiState.Success -> feedState.feed.size
    }
    // 用户引导的数量，加载中、加载失败、不展示为0，加载成功为1。
    val onboardingSize = when (onboardingUiState) {
        OnboardingUiState.Loading,
        OnboardingUiState.LoadFailed,
        OnboardingUiState.NotShown,
        -> 0

        is OnboardingUiState.Shown -> 1
    }
    return feedSize + onboardingSize
}

@DevicePreviews
@Composable
// ForYou（为你）屏-填充新闻摘要数据
// 在手机-竖屏、手机-横屏、折叠屏、平板上，展示不带引导的ForYou（为你）屏（3条数据）。
fun ForYouScreenPopulatedFeed(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.NotShown,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
// ForYou（为你）屏-离线-填充新闻摘要数据
// 在手机-竖屏、手机-横屏、折叠屏、平板上，展示不带引导的ForYou（为你）屏（3条数据）。
fun ForYouScreenOfflinePopulatedFeed(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.NotShown,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
// ForYou（为你）屏-主题选择
// 在手机-竖屏、手机-横屏、折叠屏、平板上，展示带引导的ForYou（为你）屏（3条数据）。
fun ForYouScreenTopicSelection(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.Shown(
                topics = userNewsResources.flatMap { news -> news.followableTopics }
                    .distinctBy { it.topic.id },
            ),
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
// ForYou（为你）屏-加载中
// 在手机-竖屏、手机-横屏、折叠屏、平板上，展示仅加载中的ForYou（为你）屏（3条数据）。
fun ForYouScreenLoading() {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.Loading,
            feedState = NewsFeedUiState.Loading,
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
// ForYou（为你）屏-填充和加载中
// 在手机-竖屏、手机-横屏、折叠屏、平板上，展示填充新闻摘要列表和加载中的ForYou（为你）屏（3条数据）。
fun ForYouScreenPopulatedAndLoading(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = true,
            onboardingUiState = OnboardingUiState.Loading,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}
