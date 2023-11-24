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
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.samples.apps.nowinandroid.feature.interests.InterestsRoute

private const val INTERESTS_GRAPH_ROUTE_PATTERN = "interests_graph"
const val interestsRoute = "interests_route"

// 导航到Interests（兴趣）屏
fun NavController.navigateToInterestsGraph(navOptions: NavOptions? = null) {
    this.navigate(INTERESTS_GRAPH_ROUTE_PATTERN, navOptions)
}

// Interests（兴趣）屏的导航配置（参数+UI）
fun NavGraphBuilder.interestsGraph(
    onTopicClick: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = INTERESTS_GRAPH_ROUTE_PATTERN,
        startDestination = interestsRoute,
    ) {
        composable(route = interestsRoute) {
            // Interests屏
            InterestsRoute(onTopicClick)
        }
        // topic屏
        nestedGraphs()
    }
}