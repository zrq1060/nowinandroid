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

package com.google.samples.apps.nowinandroid.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cross reference for many to many relationship between [NewsResourceEntity] and [TopicEntity]
 * [NewsResourceEntity]和[TopicEntity]之间多对多关系的交叉引用
 */
@Entity(
    // 表名
    tableName = "news_resources_topics",
    // 主键
    primaryKeys = ["news_resource_id", "topic_id"],
    // 外键，目的维护表的关系及数据完整性。
    foreignKeys = [
        // 外键，表示新闻资源（NewsResourceEntity）表的id对应此表的[news_resource_id]
        // 此外键功能：当新闻资源（NewsResourceEntity）表的新闻被删除时，则此表内和此新闻id相关的数据都会被删除。
        ForeignKey(
            entity = NewsResourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["news_resource_id"],
            // CASCADE：将父键上的删除或更新操作传播到每个依赖的子键。
            // -对于onDelete操作，这意味着子实体中与已删除的父行相关联的每一行也被删除。
            // -对于onUpdate操作，这意味着存储在每个依赖子键中的值被修改以匹配新的父键值。
            onDelete = ForeignKey.CASCADE,
        ),
        // 外键，表示主题（TopicEntity）表的id对应此表的[topic_id]
        // 此外键功能：当主题（TopicEntity）表的主题被删除时，则此表内和此主题id相关的数据都会被删除。
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    // 索引，目的是为了加快查询，但增删改会慢。
    indices = [
        Index(value = ["news_resource_id"]),
        Index(value = ["topic_id"]),
    ],
)
// 新闻资源和主题的交叉引用表（news_resources_topics），内部关联了外键、索引。
data class NewsResourceTopicCrossRef(
    @ColumnInfo(name = "news_resource_id")
    val newsResourceId: String,
    @ColumnInfo(name = "topic_id")
    val topicId: String,
)
