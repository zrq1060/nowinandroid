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

package com.google.samples.apps.nowinandroid.sync.status

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import com.google.samples.apps.nowinandroid.sync.initializers.SYNC_WORK_NAME
import com.google.samples.apps.nowinandroid.sync.workers.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [SyncManager] backed by [WorkInfo] from [WorkManager]
 * [SyncManager]由[WorkManager]的[WorkInfo]支持
 */
// SyncManager的WorkManager实现
internal class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncManager {
    // 是否同步中
    override val isSyncing: Flow<Boolean> =
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
            .map(List<WorkInfo>::anyRunning)
            .conflate()

    override fun requestSync() {
        val workManager = WorkManager.getInstance(context)
        // Run sync on app startup and ensure only one sync worker runs at any time
        // 在应用启动时运行sync，并确保任何时候只有一个同步工作线程在运行
        // 同SyncInitializer实现
        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            SyncWorker.startUpSyncWork(),
        )
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == State.RUNNING }
