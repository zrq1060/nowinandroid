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

package com.google.samples.apps.nowinandroid.core.data.repository

import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

// UserData的仓库，包含：设置用户相关（关注的主题列表、关注的一个主题、新闻资源是否已关注、新闻资源是否已读、主题种类、暗主题配置、动态颜色配置、是否已完成新用户引导过程）。
interface UserDataRepository {

    /**
     * Stream of [UserData]
     * [UserData]流
     */
    val userData: Flow<UserData>

    /**
     * Sets the user's currently followed topics
     * 设置用户-当前关注-的主题列表
     */
    suspend fun setFollowedTopicIds(followedTopicIds: Set<String>)

    /**
     * Sets the user's newly followed/unfollowed topic
     * 设置用户-新关注/取消关注-的主题
     */
    suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean)

    /**
     * Updates the bookmarked status for a news resource
     * 更新新闻资源的-书签-状态
     */
    suspend fun updateNewsResourceBookmark(newsResourceId: String, bookmarked: Boolean)

    /**
     * Updates the viewed status for a news resource
     * 更新新闻资源的-已查看-状态
     */
    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean)

    /**
     * Sets the desired theme brand.
     * 设置所需的-主题种类。
     */
    suspend fun setThemeBrand(themeBrand: ThemeBrand)

    /**
     * Sets the desired dark theme config.
     * 设置所需的-暗主题配置。
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Sets the preferred dynamic color config.
     * 设置首选的-动态颜色配置。
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)

    /**
     * Sets whether the user has completed the onboarding process.
     * 设置用户-是否已完成新用户引导过程。
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)
}
