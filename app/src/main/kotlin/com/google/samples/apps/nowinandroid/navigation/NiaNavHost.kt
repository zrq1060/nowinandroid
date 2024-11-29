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

package com.google.samples.apps.nowinandroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.google.samples.apps.nowinandroid.feature.bookmarks.navigation.bookmarksScreen
import com.google.samples.apps.nowinandroid.feature.foryou.navigation.ForYouBaseRoute
import com.google.samples.apps.nowinandroid.feature.foryou.navigation.forYouSection
import com.google.samples.apps.nowinandroid.feature.interests.navigation.navigateToInterests
import com.google.samples.apps.nowinandroid.feature.search.navigation.searchScreen
import com.google.samples.apps.nowinandroid.feature.topic.navigation.navigateToTopic
import com.google.samples.apps.nowinandroid.feature.topic.navigation.topicScreen
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination.INTERESTS
import com.google.samples.apps.nowinandroid.ui.NiaAppState
import com.google.samples.apps.nowinandroid.ui.interests2pane.interestsListDetailScreen

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 * 顶层导航图。导航的组织方式见：https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 * 这个文件中定义的导航图定义了不同的顶层路由。每个路由中的导航都是使用state和Back处理程序处理的。
 */
@Composable
// 顶层导航图，默认ForYou（为你）屏，定义了配置各个屏的交互（返回、跳转等）。
fun NiaNavHost(
    appState: NiaAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    // NavHost：
    // -在Compose层次结构中适当地提供自包含导航。
    // -一旦调用了这个函数，就可以从提供的navController导航到给定NavGraphBuilder中的任何可组合对象。
    // -传入此方法的构造器将被记住。这意味着对于这个NavHost，不能更改构建器的内容。
    // 导航配置，定义了每个屏的交互，要跳转到哪个位置。
    NavHost(
        navController = navController,
        startDestination = ForYouBaseRoute,
        modifier = modifier,
    ) {
        forYouSection(
            onTopicClick = navController::navigateToTopic,
        ) {
            topicScreen(
                showBackButton = true,
                onBackClick = navController::popBackStack,
                onTopicClick = navController::navigateToTopic,
            )
        }
        // bookmarks（书签、Saved）屏，点击主题（新闻摘要-底部水平标签）跳到Interests（兴趣/兴趣详情）屏。
        bookmarksScreen(
            onTopicClick = navController::navigateToInterests,
            onShowSnackbar = onShowSnackbar,
        )
        // search（搜索）屏，返回按钮导航返回一级，
        // 点击Interests（兴趣，搜索空结果-Interests）导航到顶级-Interests（兴趣）屏，
        // 点击主题（新闻摘要-底部水平标签）跳到Interests（兴趣/兴趣详情）屏。
        searchScreen(
            onBackClick = navController::popBackStack,
            onInterestsClick = { appState.navigateToTopLevelDestination(INTERESTS) },
            onTopicClick = navController::navigateToInterests,
        )
        // interestsListDetail（兴趣列表详情）屏。
        interestsListDetailScreen()
    }
}
