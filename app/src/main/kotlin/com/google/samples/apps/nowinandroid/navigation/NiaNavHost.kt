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
import com.google.samples.apps.nowinandroid.feature.foryou.navigation.FOR_YOU_ROUTE
import com.google.samples.apps.nowinandroid.feature.foryou.navigation.forYouScreen
import com.google.samples.apps.nowinandroid.feature.interests.navigation.interestsGraph
import com.google.samples.apps.nowinandroid.feature.search.navigation.searchScreen
import com.google.samples.apps.nowinandroid.feature.topic.navigation.navigateToTopic
import com.google.samples.apps.nowinandroid.feature.topic.navigation.topicScreen
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination.INTERESTS
import com.google.samples.apps.nowinandroid.ui.NiaAppState

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
fun NiaNavHost(
    appState: NiaAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = FOR_YOU_ROUTE,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        forYouScreen(onTopicClick = navController::navigateToTopic)
        bookmarksScreen(
            onTopicClick = navController::navigateToTopic,
            onShowSnackbar = onShowSnackbar,
        )
        // 搜索屏，route为：searchRoute。
        searchScreen(
            onBackClick = navController::popBackStack,
            onInterestsClick = { appState.navigateToTopLevelDestination(INTERESTS) },
            onTopicClick = navController::navigateToTopic,
        )
        // interests导航，route为：interestsGraphRoutePattern。
        interestsGraph(
            onTopicClick = navController::navigateToTopic,
            nestedGraphs = {
                // topic屏
                topicScreen(
                    onBackClick = navController::popBackStack,
                    onTopicClick = navController::navigateToTopic,
                )
            },
        )
    }
}
