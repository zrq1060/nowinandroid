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

import kotlinx.datetime.Instant

/**
 * A [NewsResource] with additional user information such as whether the user is following the
 * news resource's topics and whether they have saved (bookmarked) this news resource.
 * 带有附加用户信息的NewsResource，例如用户是否在关注新闻资源的主题，以及他们是否保存(书签)了此新闻资源。
 */
// 带有UserData用户信息的NewsResource新闻资源
data class UserNewsResource internal constructor(
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    val headerImageUrl: String?,
    val publishDate: Instant,
    val type: String,
    val followableTopics: List<FollowableTopic>,
    // 是否保存（加书签）的
    val isSaved: Boolean,
    val hasBeenViewed: Boolean,
) {
    // 单个NewsResource转UserNewsResource
    constructor(newsResource: NewsResource, userData: UserData) : this(
        id = newsResource.id,
        title = newsResource.title,
        content = newsResource.content,
        url = newsResource.url,
        headerImageUrl = newsResource.headerImageUrl,
        publishDate = newsResource.publishDate,
        type = newsResource.type,
        // 带有是否关注的Topic列表
        followableTopics = newsResource.topics.map { topic ->
            // Topic转FollowableTopic（带有是否关注的Topic）
            FollowableTopic(
                topic = topic,
                // 在用户关注列表里面即为关注
                isFollowed = topic.id in userData.followedTopics,
            )
        },
        // 是否保持的
        isSaved = newsResource.id in userData.bookmarkedNewsResources,
        // 是否已经观看
        hasBeenViewed = newsResource.id in userData.viewedNewsResources,
    )
}

// 批量NewsResource转UserNewsResource
fun List<NewsResource>.mapToUserNewsResources(userData: UserData): List<UserNewsResource> =
    map { UserNewsResource(it, userData) }
