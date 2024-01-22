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

import com.google.samples.apps.nowinandroid.core.data.Synchronizer
import com.google.samples.apps.nowinandroid.core.data.changeListSync
import com.google.samples.apps.nowinandroid.core.data.model.asEntity
import com.google.samples.apps.nowinandroid.core.database.dao.TopicDao
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.database.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.datastore.ChangeListVersions
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [TopicsRepository].
 * Reads are exclusively from local storage to support offline access.
 * 磁盘存储支持TopicsRepository的实现。读取仅来自本地存储，以支持离线访问。
 */
// 主题的仓库（TopicsRepository）-离线优先实现。使用数据库实现。同步操作使用网络+数据库实现。
internal class OfflineFirstTopicsRepository @Inject constructor(
    private val topicDao: TopicDao,
    private val network: NiaNetworkDataSource,
) : TopicsRepository {

    override fun getTopics(): Flow<List<Topic>> =
        // 获取全部的主题，并转换为外部（Repository层）Model。
        topicDao.getTopicEntities()
            .map { it.map(TopicEntity::asExternalModel) }

    override fun getTopic(id: String): Flow<Topic> =
        // 获取指定Id的主题，并转换为外部（Repository层）Model。
        topicDao.getTopicEntity(id).map { it.asExternalModel() }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        // 同步主题
        synchronizer.changeListSync(
            // 获取主题的当前版本号
            versionReader = ChangeListVersions::topicVersion,
            // 获取主题的改变列表，此版本之后的。
            changeListFetcher = { currentVersion ->
                // 网络获取，使用协程等待其结果。
                network.getTopicChangeList(after = currentVersion)
            },
            // 版本更新（此操作在modelDeleter、modelUpdater后调用），更新ChangeListVersions的主题的版本为最新的。
            versionUpdater = { latestVersion ->
                // 复制一个新的ChangeListVersions，主题版本为最新。
                copy(topicVersion = latestVersion)
            },
            // 删除本地主题Ids列表
            modelDeleter = topicDao::deleteTopics,
            // 更新主题Ids列表，需先从网络获取到此更新主题Ids列表的完整主题数据，再进行本地保存操作。
            modelUpdater = { changedIds ->
                // 从网络获取到此更新主题Ids列表的完整主题数据
                val networkTopics = network.getTopics(ids = changedIds)
                // 本地保存
                topicDao.upsertTopics(
                    entities = networkTopics.map(NetworkTopic::asEntity),
                )
            },
        )
}
