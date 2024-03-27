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

package com.google.samples.apps.nowinandroid.core.data

import android.util.Log
import com.google.samples.apps.nowinandroid.core.datastore.ChangeListVersions
import com.google.samples.apps.nowinandroid.core.network.model.NetworkChangeList
import kotlin.coroutines.cancellation.CancellationException

/**
 * Interface marker for a class that manages synchronization between local data and a remote
 * source for a [Syncable].
 * 接口标记，用于管理[Syncable]的本地数据与远程源之间的同步的类。
 */
// 接口标记，用于管理被[Syncable]标记的类的同步。
interface Synchronizer {
    suspend fun getChangeListVersions(): ChangeListVersions

    suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions)

    /**
     * Syntactic sugar to call [Syncable.syncWith] while omitting the synchronizer argument
     * 调用[Syncable.syncWith]的语法糖，同时省略同步器参数
     */
    // 在此Synchronizer（如：SyncWorker）内调用某个Syncable（如：NewsRepository、TopicsRepository）的sync方法，即可调用此Syncable的syncWith方法并传递此Synchronizer参数。
    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)
}

/**
 * Interface marker for a class that is synchronized with a remote source. Syncing must not be
 * performed concurrently and it is the [Synchronizer]'s responsibility to ensure this.
 * 与远程源同步的类的接口标记。同步不能并发执行，确保这一点是[Synchronizer]的责任。
 */
// 接口标记，用于标记需要同步。
interface Syncable {
    /**
     * Synchronizes the local database backing the repository with the network.
     * Returns if the sync was successful or not.
     * 将支持存储库的本地数据库与网络同步。返回同步是否成功。
     */
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 * 尝试[block]，如果成功返回一个成功的[Result]，否则返回一个[Result.Failure]，注意不要破坏结构化并发
 */
// try-catch转Result
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.i(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception,
    )
    Result.failure(exception)
}

/**
 * Utility function for syncing a repository with the network.
 * 用于将存储库与网络同步的实用程序函数。
 * [versionReader] Reads the current version of the model that needs to be synced
 *                  读取需要同步的模型的当前版本
 * [changeListFetcher] Fetches the change list for the model
 *                  获取模型的变更列表
 * [versionUpdater] Updates the [ChangeListVersions] after a successful sync
 *                  同步成功后更新[ChangeListVersions]
 * [modelDeleter] Deletes models by consuming the ids of the models that have been deleted.
 *                  通过使用已删除模型的id来删除模型。
 * [modelUpdater] Updates models by consuming the ids of the models that have changed.
 *                  通过使用已更改的模型的id来更新模型。
 *
 * Note that the blocks defined above are never run concurrently, and the [Synchronizer]
 * implementation must guarantee this.
 * 注意，上面定义的块永远不会并发运行，[Synchronizer]实现必须保证这一点。
 */
// 同步
suspend fun Synchronizer.changeListSync(
    // 版本读取者，用于获取当前（已经更改）的版本号（此ChangeListVersions内保存了topicVersion、newsResourceVersion的版本）。
    versionReader: (ChangeListVersions) -> Int,
    // 改变列表获取者，用于获取当前改变的列表（应该是获取当前版本号（此Int参数）之后的改变（要更新或者要删除）的列表）。
    changeListFetcher: suspend (Int) -> List<NetworkChangeList>,
    // 版本更新者，用于同步完全部数据后，更新一次本地的版本号（参数Int为最新的版本，需要返回ChangeListVersions）。
    versionUpdater: ChangeListVersions.(Int) -> ChangeListVersions,
    // model删除者，用于删除本地的列表内数据。
    modelDeleter: suspend (List<String>) -> Unit,
    // model更新者，用于更新本地的列表内数据。
    modelUpdater: suspend (List<String>) -> Unit,
) = suspendRunCatching {
    // Fetch the change list since last sync (akin to a git fetch)
    // 获取自上次同步以来的更改列表(类似于git fetch)
    // 当前版本号
    val currentVersion = versionReader(getChangeListVersions())
    // 改变的列表，包括删除、更新。
    val changeList = changeListFetcher(currentVersion)
    // 改变的列表为空，直接返回。
    if (changeList.isEmpty()) return@suspendRunCatching true

    // 分区，把changeList根据isDelete分成：deleted要删除的列表、updated要更新的列表。
    val (deleted, updated) = changeList.partition(NetworkChangeList::isDelete)

    // Delete models that have been deleted server-side
    // 删除已经在服务器端删除的模型
    modelDeleter(deleted.map(NetworkChangeList::id))

    // Using the change list, pull down and save the changes (akin to a git pull)
    // 使用变更列表，pull并保存更改(类似于git pull)
    modelUpdater(updated.map(NetworkChangeList::id))

    // Update the last synced version (akin to updating local git HEAD)
    // 更新上一次同步的版本(类似于更新本地git HEAD)
    // 最后的版本，因为版本可能跨越多个，所以需要使用网络版本+1，而不是本地版本+1。
    val latestVersion = changeList.last().changeListVersion
    // 更新本地版本为最新
    updateChangeListVersions {
        // 获取最新版本（latestVersion）的ChangeListVersions
        versionUpdater(latestVersion)
    }
}.isSuccess