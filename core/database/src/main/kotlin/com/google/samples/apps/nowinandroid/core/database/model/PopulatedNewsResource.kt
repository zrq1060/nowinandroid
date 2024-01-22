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

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource

/**
 * External data layer representation of a fully populated NiA news resource
 * 一个完全填充的NiA新闻资源的外部数据层表示
 */
// 填充的新闻资源，包括此信息资源（NewsResourceEntity）的解体数据和主题列表（topics）的数据。
// 说明：新闻资源NewsResourceEntity和主题TopicEntity是多对多的关系，此处是其中一个的实现。
data class PopulatedNewsResource(
    // 嵌入式，内容会解体。
    @Embedded
    // 新闻资源
    val entity: NewsResourceEntity,
    // 关系表
    @Relation(
        // 父实体主键列的名称，为NewsResourceEntity解体的id。
        parentColumn = "id",
        // 子实体列的名称，为TopicEntity的id。
        entityColumn = "id",
        // 交叉引用实体，使用 associateBy 属性来确定提供 NewsResourceEntity 实体与 TopicEntity 实体之间关系的交叉引用实体。
        associateBy = Junction(
            // 此为关系的交叉引用实体
            value = NewsResourceTopicCrossRef::class,
            // 此NewsResourceTopicCrossRef内的news_resource_id，对应NewsResourceEntity的id。
            parentColumn = "news_resource_id",
            // 此NewsResourceTopicCrossRef内的topic_id，对应TopicEntity的id。
            entityColumn = "topic_id",
        ),
    )
    // 主题列表数据，
    val topics: List<TopicEntity>,
)

// PopulatedNewsResource（填充的新闻资源），组合类->外部类。
fun PopulatedNewsResource.asExternalModel() = NewsResource(
    id = entity.id,
    title = entity.title,
    content = entity.content,
    url = entity.url,
    headerImageUrl = entity.headerImageUrl,
    publishDate = entity.publishDate,
    type = entity.type,
    topics = topics.map(TopicEntity::asExternalModel),
)

// PopulatedNewsResource（填充的新闻资源），组合类->数据库类。
fun PopulatedNewsResource.asFtsEntity() = NewsResourceFtsEntity(
    newsResourceId = entity.id,
    title = entity.title,
    content = entity.content,
)
