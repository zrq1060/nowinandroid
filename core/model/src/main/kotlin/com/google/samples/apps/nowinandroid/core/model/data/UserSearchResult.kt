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

package com.google.samples.apps.nowinandroid.core.model.data

/**
 * An entity of [SearchResult] with additional user information such as whether the user is
 * following a topic.
 * [SearchResult]的一个实体，包含附加的用户信息，比如用户是否在关注某个主题。
 */
// 用户搜索结果，包含：FollowableTopic（带有是否关注的Topic）列表、UserNewsResource（带有UserData用户信息的NewsResource新闻资源）列表。
data class UserSearchResult(
    val topics: List<FollowableTopic> = emptyList(),
    val newsResources: List<UserNewsResource> = emptyList(),
)
