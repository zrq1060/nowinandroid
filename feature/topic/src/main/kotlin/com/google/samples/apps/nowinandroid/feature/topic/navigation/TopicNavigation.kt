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

package com.google.samples.apps.nowinandroid.feature.topic.navigation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.samples.apps.nowinandroid.feature.topic.TopicRoute
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

private val URL_CHARACTER_ENCODING = UTF_8.name()

@VisibleForTesting
internal const val TOPIC_ID_ARG = "topicId"

// Topic（主题）参数，topicId为savedStateHandle[TOPIC_ID_ARG]的解码值。
internal class TopicArgs(val topicId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(URLDecoder.decode(checkNotNull(savedStateHandle[TOPIC_ID_ARG]), URL_CHARACTER_ENCODING))
}

// 导航控制-导航到Topic（主题）屏
fun NavController.navigateToTopic(topicId: String) {
    val encodedId = URLEncoder.encode(topicId, URL_CHARACTER_ENCODING)
    navigate("topic_route/$encodedId") {
        launchSingleTop = true
    }
}

// 导航图构建-Topic（主题）屏（参数+UI）
fun NavGraphBuilder.topicScreen(
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
) {
    composable(
        route = "topic_route/{$TOPIC_ID_ARG}",
        // 参数，topicId String类型。
        arguments = listOf(
            navArgument(TOPIC_ID_ARG) { type = NavType.StringType },
        ),
    ) {
        // Topic（主题）屏-Route（ViewModel+UI）
        TopicRoute(onBackClick = onBackClick, onTopicClick = onTopicClick)
    }
}
