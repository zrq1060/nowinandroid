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

import com.google.samples.apps.nowinandroid.core.data.Syncable
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import kotlinx.coroutines.flow.Flow

// Topic的仓库，包含：获取全部主题、获取指定Id的主题。
interface TopicsRepository : Syncable {
    /**
     * Gets the available topics as a stream
     * 以流的形式-获取可用主题
     */
    // 获取全部主题
    fun getTopics(): Flow<List<Topic>>

    /**
     * Gets data for a specific topic
     * 获取指定主题的数据
     */
    // 获取指定Id的主题
    fun getTopic(id: String): Flow<Topic>
}
