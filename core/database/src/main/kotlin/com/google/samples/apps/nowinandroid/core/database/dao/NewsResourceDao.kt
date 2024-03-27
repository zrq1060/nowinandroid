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

package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceTopicCrossRef
import com.google.samples.apps.nowinandroid.core.database.model.PopulatedNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [NewsResource] and [NewsResourceEntity] access
 * 用于[NewsResource]和[NewsResourceEntity]访问的DAO
 */
@Dao
// 新闻资源相关表（news_resources、news_resources_topics）的操作。包含：获取新闻资源（ID、PopulatedNewsResource）的列表、
// 插入或忽略（如果存在则忽略）指定newsResourceEntities、更新插入（如果存在则替换）指定newsResourceEntities、插入或忽略（如果存在则忽略）指定newsResourceTopicCrossReferences、删除指定ids的NewsResources。
interface NewsResourceDao {

    /**
     * Fetches news resources that match the query parameters
     * 获取与查询参数匹配的新闻资源
     */
    // 因为查询的是3次（3个表），所以用Transaction事务。
    @Transaction
    // 查询新闻资源表，可支持过滤TopicIds、NewsIds，并按照时间降序（最近的在前）。
    // 分析查询：
    // -返回的是PopulatedNewsResource列表，
    // -1.其内部关联了NewsResourceEntity（news_resources表），所以此需要查询的是新闻资源表（news_resources）；
    // -2.其内部关联了TopicEntity（topics表），此两个表的关系通过NewsResourceTopicCrossRef（news_resources_topics表）来进行关联。
    // -总结流程：
    // --1.查询news_resources表，获取到全部的新闻（NewsResourceEntity）列表。
    // --2.通过新闻ID再在news_resources_topics表（关系表），获取到此新闻ID关联的主题ID列表。
    // --3.通过主题ID列表再在topics表，获取到此主题ID列表的全部主题（TopicEntity）列表。
    @Query(
        value = """
            SELECT * FROM news_resources
            WHERE 
                CASE WHEN :useFilterNewsIds
                    THEN id IN (:filterNewsIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterTopicIds
                    THEN id IN
                        (
                            SELECT news_resource_id FROM news_resources_topics
                            WHERE topic_id IN (:filterTopicIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    // 获取新闻资源PopulatedNewsResource的列表，可过滤TopicIds、NewsIds（两者是并且的关系）。
    fun getNewsResources(
        useFilterTopicIds: Boolean = false,
        filterTopicIds: Set<String> = emptySet(),
        useFilterNewsIds: Boolean = false,
        filterNewsIds: Set<String> = emptySet(),
    ): Flow<List<PopulatedNewsResource>>

    /**
     * Fetches ids of news resources that match the query parameters
     * 获取与查询参数匹配的新闻资源的id
     */
    @Transaction
    // 查询语句同上，除第一句：SELECT id FROM news_resources 外（两者是并且的关系）。
    @Query(
        value = """
            SELECT id FROM news_resources
            WHERE 
                CASE WHEN :useFilterNewsIds
                    THEN id IN (:filterNewsIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterTopicIds
                    THEN id IN
                        (
                            SELECT news_resource_id FROM news_resources_topics
                            WHERE topic_id IN (:filterTopicIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    // 获取新闻资源Id的列表，可过滤TopicIds、NewsIds。
    fun getNewsResourceIds(
        useFilterTopicIds: Boolean = false,
        filterTopicIds: Set<String> = emptySet(),
        useFilterNewsIds: Boolean = false,
        filterNewsIds: Set<String> = emptySet(),
    ): Flow<List<String>>

    /**
     * Inserts or updates [newsResourceEntities] in the db under the specified primary keys
     * 在指定的主键下插入或更新数据库中的[newsResourceEntities]
     */
    @Upsert
    // 更新插入（如果存在则替换）指定newsResourceEntities的NewsResourceEntity
    suspend fun upsertNewsResources(newsResourceEntities: List<NewsResourceEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    // 插入或忽略（如果存在则忽略）指定newsResourceTopicCrossReferences的NewsResourceTopicCrossRef
    suspend fun insertOrIgnoreTopicCrossRefEntities(
        newsResourceTopicCrossReferences: List<NewsResourceTopicCrossRef>,
    )

    /**
     * Deletes rows in the db matching the specified [ids]
     * 删除数据库中与指定[ids]匹配的行。
     */
    @Query(
        value = """
            DELETE FROM news_resources
            WHERE id in (:ids)
        """,
    )
    // 删除指定ids的NewsResources
    suspend fun deleteNewsResources(ids: List<String>)
}
