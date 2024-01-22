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

package com.google.samples.apps.nowinandroid.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.google.samples.apps.nowinandroid.R
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.util.NetworkMonitor
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaBackground
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaGradientBackground
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaNavigationBar
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaNavigationBarItem
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaNavigationRail
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaNavigationRailItem
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTopAppBar
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.GradientColors
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LocalGradientColors
import com.google.samples.apps.nowinandroid.feature.settings.SettingsDialog
import com.google.samples.apps.nowinandroid.navigation.NiaNavHost
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination
import com.google.samples.apps.nowinandroid.feature.settings.R as settingsR

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
// AppUI，包括背景、无网络提示、设置Dialog、一级导航（水平+垂直）、标题栏、顶层导航图。
fun NiaApp(
    // window大小
    windowSizeClass: WindowSizeClass,
    // 网络监控
    networkMonitor: NetworkMonitor,
    // 用户新闻资源库
    userNewsResourceRepository: UserNewsResourceRepository,
    // app状态
    appState: NiaAppState = rememberNiaAppState(
        networkMonitor = networkMonitor,
        windowSizeClass = windowSizeClass,
        userNewsResourceRepository = userNewsResourceRepository,
    ),
) {
    // 是否展示渐变背景（只有ForYou（为你）屏展示）
    val shouldShowGradientBackground =
        appState.currentTopLevelDestination == TopLevelDestination.FOR_YOU
    // 是否展示设置Dialog（默认不展示）
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    NiaBackground {
        NiaGradientBackground(
            // 背景颜色
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {
            // SnackbarHost的状态，它控制队列和在SnackbarHost中显示的当前Snackbar。
            // 此状态通常被记住并用于向Scaffold提供SnackbarHost。
            val snackbarHostState = remember { SnackbarHostState() }

            // 是否是离线网络
            val isOffline by appState.isOffline.collectAsStateWithLifecycle()

            // If user is not connected to the internet show a snack bar to inform them.
            // 如果用户没有连接到互联网，显示一个Snackbar通知他们。
            val notConnectedMessage = stringResource(R.string.not_connected)
            // 启动效果，无网，展示提示。
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = notConnectedMessage,
                        // 无限期展示
                        duration = Indefinite,
                    )
                }
            }

            if (showSettingsDialog) {
                // 展示设置Dialog
                SettingsDialog(
                    // 销毁请求，即不展示请求，设置showSettingsDialog = false。
                    onDismiss = { showSettingsDialog = false },
                )
            }

            // 所有未读的目的地
            val unreadDestinations by appState.topLevelDestinationsWithUnreadResources.collectAsStateWithLifecycle()

            Scaffold(
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (appState.shouldShowBottomBar) {
                        // 展示底部bar（底部bar和侧边bar只能二选一）。
                        NiaBottomBar(
                            // 所有顶级目的地，TopLevelDestination枚举类集合。
                            destinations = appState.topLevelDestinations,
                            // 所有未读的目的地
                            destinationsWithUnreadResources = unreadDestinations,
                            // 切换item跳到目的地
                            onNavigateToDestination = appState::navigateToTopLevelDestination,
                            currentDestination = appState.currentDestination,
                            modifier = Modifier.testTag("NiaBottomBar"),
                        )
                    }
                },
            ) { padding ->
                // 行，为了兼容横屏UI。
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal,
                            ),
                        ),
                ) {
                    if (appState.shouldShowNavRail) {
                        // 展示侧边bar（底部bar和侧边bar只能二选一）。
                        // 参数同NiaBottomBar。
                        NiaNavRail(
                            destinations = appState.topLevelDestinations,
                            destinationsWithUnreadResources = unreadDestinations,
                            onNavigateToDestination = appState::navigateToTopLevelDestination,
                            currentDestination = appState.currentDestination,
                            modifier = Modifier
                                .testTag("NiaNavRail")
                                .safeDrawingPadding(),
                        )
                    }

                    // 栏目内容（列），通用（包括横屏和竖屏）。
                    Column(Modifier.fillMaxSize()) {
                        // Show the top app bar on top level destinations.
                        // 在顶级目的地显示顶级应用程序栏。
                        val destination = appState.currentTopLevelDestination
                        if (destination != null) {
                            // 顶部标题栏（搜索、标题、设置）
                            NiaTopAppBar(
                                // 标题
                                titleRes = destination.titleTextId,
                                // 搜索
                                navigationIcon = NiaIcons.Search,
                                navigationIconContentDescription = stringResource(
                                    id = settingsR.string.feature_settings_top_app_bar_navigation_icon_description,
                                ),
                                // 设置
                                actionIcon = NiaIcons.Settings,
                                actionIconContentDescription = stringResource(
                                    id = settingsR.string.feature_settings_top_app_bar_action_icon_description,
                                ),
                                // 颜色
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.Transparent,
                                ),
                                // 设置点击
                                onActionClick = { showSettingsDialog = true },
                                // 搜索点击
                                onNavigationClick = { appState.navigateToSearch() },
                            )
                        }

                        // 顶层导航图，默认ForYou（为你）屏。
                        NiaNavHost(appState = appState, onShowSnackbar = { message, action ->
                            // 展示Snackbar，并返回是否需要撤销操作，返回true代表需要撤销书签移除（内部会进行恢复）。
                            // ActionPerformed：在超时之前，单击了Snackbar上的操作。
                            snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = action,
                                duration = Short,
                            ) == ActionPerformed
                        })
                    }

                    // TODO: We may want to add padding or spacer when the snackbar is shown so that
                    //  content doesn't display behind it.
                    // 我们可能想要在显示snackbar时添加填充或间隔，这样内容就不会显示在它后面。
                }
            }
        }
    }
}

// 侧边bar
// 参数同NiaBottomBar。
@Composable
private fun NiaNavRail(
    destinations: List<TopLevelDestination>,
    destinationsWithUnreadResources: Set<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    NiaNavigationRail(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            val hasUnread = destinationsWithUnreadResources.contains(destination)
            NiaNavigationRailItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.iconTextId)) },
                modifier = if (hasUnread) Modifier.notificationDot() else Modifier,
            )
        }
    }
}

// 底部bar
@Composable
private fun NiaBottomBar(
    destinations: List<TopLevelDestination>,
    destinationsWithUnreadResources: Set<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    NiaNavigationBar(
        modifier = modifier,
    ) {
        destinations.forEach { destination ->
            // 设置当前的Item
            // 是否有未读
            val hasUnread = destinationsWithUnreadResources.contains(destination)
            // 是否选中
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NiaNavigationBarItem(
                selected = selected,
                // 点击导航
                onClick = { onNavigateToDestination(destination) },
                // 未选中的Icon
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                // 选中的Icon
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null,
                    )
                },
                // 标签
                label = { Text(stringResource(destination.iconTextId)) },
                // 是否有未读消息
                modifier = if (hasUnread) Modifier.notificationDot() else Modifier,
            )
        }
    }
}

// 通知点
private fun Modifier.notificationDot(): Modifier =
    composed {
        // 三级颜色
        val tertiaryColor = MaterialTheme.colorScheme.tertiary
        drawWithContent {
            // 内容在下，点在上。
            // 画内容
            drawContent()
            // 画圆
            drawCircle(
                tertiaryColor,
                radius = 5.dp.toPx(),
                // This is based on the dimensions of the NavigationBar's "indicator pill";
                // however, its parameters are private, so we must depend on them implicitly
                // (NavigationBarTokens.ActiveIndicatorWidth = 64.dp)
                // 键入应用程序中的顶级目的地。这些目的地中的每个都可以包含一个或多个屏幕(基于窗口大小)。在单个目的地内从一个屏幕导航到下一个屏幕将直接在可组合物中处理。
                center = center + Offset(
                    64.dp.toPx() * .45f,
                    32.dp.toPx() * -.45f - 6.dp.toPx(),
                ),
            )
        }
    }

// 是否顶层目的地在层次结构中
private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false
