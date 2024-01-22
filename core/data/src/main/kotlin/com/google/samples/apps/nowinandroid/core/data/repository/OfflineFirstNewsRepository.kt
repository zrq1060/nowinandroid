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
import com.google.samples.apps.nowinandroid.core.data.model.topicCrossReferences
import com.google.samples.apps.nowinandroid.core.data.model.topicEntityShells
import com.google.samples.apps.nowinandroid.core.database.dao.NewsResourceDao
import com.google.samples.apps.nowinandroid.core.database.dao.TopicDao
import com.google.samples.apps.nowinandroid.core.database.model.PopulatedNewsResource
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.database.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.datastore.ChangeListVersions
import com.google.samples.apps.nowinandroid.core.datastore.NiaPreferencesDataSource
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.notifications.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Heuristic value to optimize for serialization and deserialization cost on client and server
// for each news resource batch.
// 为每个新闻资源批在客户端和服务器上优化序列化和反序列化成本的启发式值。
private const val SYNC_BATCH_SIZE = 40

/**
 * Disk storage backed implementation of the [NewsRepository].
 * Reads are exclusively from local storage to support offline access.
 * 磁盘存储支持NewsRepository的实现。读取仅来自本地存储，以支持离线访问。
 */
// 新闻的仓库（NewsRepository）-离线优先实现。使用数据库实现。同步操作使用网络+数据库实现。
internal class OfflineFirstNewsRepository @Inject constructor(
    private val niaPreferencesDataSource: NiaPreferencesDataSource,
    private val newsResourceDao: NewsResourceDao,
    private val topicDao: TopicDao,
    private val network: NiaNetworkDataSource,
    private val notifier: Notifier,
) : NewsRepository {

    override fun getNewsResources(
        query: NewsResourceQuery,
        // newsResourceDao获取填充新闻资源PopulatedNewsResource列表（查询了3个表），并转为NewsResource（仓库层使用）。
    ): Flow<List<NewsResource>> = newsResourceDao.getNewsResources(
        useFilterTopicIds = query.filterTopicIds != null,
        filterTopicIds = query.filterTopicIds ?: emptySet(),
        useFilterNewsIds = query.filterNewsIds != null,
        filterNewsIds = query.filterNewsIds ?: emptySet(),
    )
        .map { it.map(PopulatedNewsResource::asExternalModel) }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        // 同步新闻
        // 是否是第一次同步
        var isFirstSync = false
        return synchronizer.changeListSync(
            // 获取新闻的当前版本号
            versionReader = ChangeListVersions::newsResourceVersion,
            // 获取新闻的改变列表，此版本之后的。
            changeListFetcher = { currentVersion ->
                // 是否是第一次同步，当前版本小于0即是，因为默认为-1。
                isFirstSync = currentVersion <= 0
                // 网络获取，使用协程等待其结果。
                network.getNewsResourceChangeList(after = currentVersion)
            },
            // 版本更新（此操作在modelDeleter、modelUpdater后调用），更新ChangeListVersions的新闻的版本为最新的。
            versionUpdater = { latestVersion ->
                // 复制一个新的ChangeListVersions，新闻版本为最新。
                copy(newsResourceVersion = latestVersion)
            },
            // 删除本地新闻Ids列表
            modelDeleter = newsResourceDao::deleteNewsResources,
            // 更新本地新闻Ids列表，需先从网络获取到此更新新闻Ids列表的完整新闻数据，再进行本地保存操作。
            modelUpdater = { changedIds ->
                // 用户数据，最新的。
                val userData = niaPreferencesDataSource.userData.first()
                // 是否已经新用户引导（true则FouYou屏不展示引导）
                val hasOnboarded = userData.shouldHideOnboarding
                // 已关注的主题Id列表
                val followedTopicIds = userData.followedTopics

                // 存在的新闻资源ids已更改，用于通知用户新闻更新。
                val existingNewsResourceIdsThatHaveChanged = when {
                    // 已经新用户引导（true则FouYou屏不展示引导），获取新闻资源Ids。
                    hasOnboarded -> newsResourceDao.getNewsResourceIds(
                        useFilterTopicIds = true,
                        filterTopicIds = followedTopicIds,
                        useFilterNewsIds = true,
                        filterNewsIds = changedIds.toSet(),
                    )
                        .first()
                        .toSet()
                    // No need to retrieve anything if notifications won't be sent
                    // 如果通知不发送，则不需要检索任何内容
                    else -> emptySet()
                }

                // 说明：isFirstSync，因为先调用的changeListFetcher，后调用的此modelUpdater，所以此一定走了isFirstSync的赋值判断。
                if (isFirstSync) {
                    // When we first retrieve news, mark everything viewed, so that we aren't
                    // overwhelmed with all historical news.
                    // 当我们第一次检索新闻时，标记所有内容已浏览的，这样我们就不会被所有的历史新闻淹没。
                    niaPreferencesDataSource.setNewsResourcesViewed(changedIds, true)
                }

                // Obtain the news resources which have changed from the network and upsert them locally
                // 获取网络上发生变化的新闻资源，并在本地进行upsert

                // 改变列表分组，每组最多40个，然后遍历每组，进行同时分组获取数据。
                changedIds.chunked(SYNC_BATCH_SIZE).forEach { chunkedIds ->
                    // 从网络获取到此更新新闻Ids列表的完整新闻数据
                    val networkNewsResources = network.getNewsResources(ids = chunkedIds)

                    // Order of invocation matters to satisfy id and foreign key constraints!
                    // 调用顺序对满足id和外键约束很重要!

                    // 插入或忽略（如果存在则忽略）指定topicEntities，主题变化的慢，以本地的为主，所以有冲突要进行忽略。
                    topicDao.insertOrIgnoreTopics(
                        // 网络的新闻资源列表，Item的topics转TopicEntity列表（即列表嵌套列表），再进行合并到一个集合，最后根据主题Id去重（因为不同的新闻资源可能会有相同的主题Id）。
                        topicEntities = networkNewsResources
                            .map(NetworkNewsResource::topicEntityShells)
                            .flatten()
                            .distinctBy(TopicEntity::id),
                    )
                    // 更新插入（如果存在则替换）指定newsResourceEntities，新闻资源变化的快，以网络的为主，所以有冲突要进行替换。
                    newsResourceDao.upsertNewsResources(
                        // 网络的新闻资源列表，转NewsResourceEntity列表。
                        newsResourceEntities = networkNewsResources.map(
                            NetworkNewsResource::asEntity,
                        ),
                    )
                    // 注意：NewsResourceTopicCrossRef的插入，要放到TopicEntity表、NewsResourceEntity表后面，因为这个有外键，关联了此两个表。
                    // 插入或忽略（如果存在则忽略）指定TopicCrossRefEntities，因为此表内就2个主键，所以存在冲突，说明两个的值相等了（即TopicCrossRefEntities的值相等了），就忽略不需要修改。
                    newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
                        // 网络的新闻资源列表，Item的topics转NewsResourceTopicCrossRef列表（即列表嵌套列表），再去重（因为不同的新闻资源可能会有相同的主题Id），最后进行合并到一个集合。
                        newsResourceTopicCrossReferences = networkNewsResources
                            .map(NetworkNewsResource::topicCrossReferences)
                            .distinct()
                            .flatten(),
                    )
                }

                if (hasOnboarded) {
                    // 已经新用户引导（true则FouYou屏不展示引导），获取新闻资源列表。
                    val addedNewsResources = newsResourceDao.getNewsResources(
                        useFilterTopicIds = true,
                        filterTopicIds = followedTopicIds,
                        useFilterNewsIds = true,
                        filterNewsIds = changedIds.toSet() - existingNewsResourceIdsThatHaveChanged,
                    )
                        .first()
                        .map(PopulatedNewsResource::asExternalModel)

                    if (addedNewsResources.isNotEmpty()) {
                        // 增加的新闻资源，不为空，则进行通知。
                        notifier.postNewsNotifications(
                            newsResources = addedNewsResources,
                        )
                    }
                }
            },
        )
    }
}
