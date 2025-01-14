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

package com.google.samples.apps.nowinandroid.sync.initializers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.google.samples.apps.nowinandroid.sync.workers.SyncWorker

// 同步初始化，SyncWorker入队。
object Sync {
    // This method is initializes sync, the process that keeps the app's data current.
    // It is called from the app module's Application.onCreate() and should be only done once.
    // 这个方法初始化sync，这个进程保持应用程序的数据是最新的。
    // 它从应用模块的Application.onCreate()调用，应该只执行一次。
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            // Run sync on app startup and ensure only one sync worker runs at any time
            // 在应用启动时运行sync，并确保任何时候只有一个同步工作线程在运行
            enqueueUniqueWork(
                SYNC_WORK_NAME,
                // ExistingWorkPolicy.KEEP：如果存在具有相同唯一名称的挂起(未完成)工作，则不执行任何操作。否则，插入新指定的作品。
                ExistingWorkPolicy.KEEP,
                // 开启Work
                SyncWorker.startUpSyncWork(),
            )
        }
    }
}

// This name should not be changed otherwise the app may have concurrent sync requests running
// 此名称不应更改，否则应用程序可能有并发同步请求正在运行
internal const val SYNC_WORK_NAME = "SyncWorkName"
