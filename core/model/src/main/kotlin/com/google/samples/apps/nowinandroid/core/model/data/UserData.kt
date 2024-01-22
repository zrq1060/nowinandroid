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

package com.google.samples.apps.nowinandroid.core.model.data

/**
 * Class summarizing user interest data
 * 类汇总用户兴趣数据
 */
// 用户数据
data class UserData(
    // 已加入书签的-新闻资源Id列表
    val bookmarkedNewsResources: Set<String>,
    // 已浏览的-新闻资源Id列表
    val viewedNewsResources: Set<String>,
    // 已关注的-主题Id列表
    val followedTopics: Set<String>,
    // 主题品牌
    val themeBrand: ThemeBrand,
    // 暗主题配置
    val darkThemeConfig: DarkThemeConfig,
    // 是否使用动态颜色
    val useDynamicColor: Boolean,
    // 是否隐藏新用户引导流程（ForYou屏上面的）
    val shouldHideOnboarding: Boolean,
)
