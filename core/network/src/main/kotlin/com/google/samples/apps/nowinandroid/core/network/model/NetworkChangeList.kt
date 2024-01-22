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

package com.google.samples.apps.nowinandroid.core.network.model

import kotlinx.serialization.Serializable

/**
 * Network representation of a change list for a model.
 * 模型变更列表的网络表示形式。
 *
 * Change lists are a representation of a server-side map like data structure of model ids to
 * metadata about that model. In a single change list, a given model id can only show up once.
 * 变更列表是服务器端映射的表示，例如模型id的数据结构到关于该模型的元数据。
 * 在单个更改列表中，给定的模型id只能显示一次。
 */
@Serializable
// 网络的改变列表
data class NetworkChangeList(
    /**
     * The id of the model that was changed
     * 被更改的模型id
     */
    val id: String,
    /**
     * Unique consecutive, monotonically increasing version number in the collection describing
     * the relative point of change between models in the collection
     * 集合中唯一连续的、单调递增的版本号，描述集合中模型之间的相对变化点
     */
    val changeListVersion: Int,
    /**
     * Summarizes the update to the model; whether it was deleted or updated.
     * Updates include creations.
     * 总结了对模型的更新;是否被删除或更新。更新包括创建。
     */
    val isDelete: Boolean,
)
