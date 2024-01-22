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

package com.google.samples.apps.nowinandroid.core.analytics

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Global key used to obtain access to the AnalyticsHelper through a CompositionLocal.
 * 全局键，用于通过CompositionLocal获取对AnalyticsHelper的访问。
 */
val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    // Provide a default AnalyticsHelper which does nothing. This is so that tests and previews
    // do not have to provide one. For real app builds provide a different implementation.
    // 提供一个默认的什么都不做的AnalyticsHelper。这样测试和预览就不必提供一个。对于真正的应用构建提供不同的实现。
    NoOpAnalyticsHelper()
}
