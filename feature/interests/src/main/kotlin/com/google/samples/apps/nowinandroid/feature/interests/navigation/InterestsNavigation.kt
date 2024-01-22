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

package com.google.samples.apps.nowinandroid.feature.interests.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.samples.apps.nowinandroid.feature.interests.InterestsRoute

private const val INTERESTS_GRAPH_ROUTE_PATTERN = "interests_graph"
// Interests（兴趣）屏的Route
const val INTERESTS_ROUTE = "interests_route"

// 导航控制-导航到Interests（兴趣）图
fun NavController.navigateToInterestsGraph(navOptions: NavOptions) = navigate(INTERESTS_GRAPH_ROUTE_PATTERN, navOptions)

// 导航图构建-Interests（兴趣）图（参数+UI）
fun NavGraphBuilder.interestsGraph(
    onTopicClick: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    // 嵌套导航图，开始目的地为Interests（兴趣）屏。
    navigation(
        route = INTERESTS_GRAPH_ROUTE_PATTERN,
        startDestination = INTERESTS_ROUTE,
    ) {
        // Interests（兴趣）屏
        composable(route = INTERESTS_ROUTE) {
            // Interests（兴趣）屏-Route（ViewModel+UI）
            InterestsRoute(onTopicClick)
        }
        // 嵌套图
        nestedGraphs()
    }
}
