/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.util

import android.util.Log
import androidx.profileinstaller.ProfileVerifier
import com.google.samples.apps.nowinandroid.core.network.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Logs the app's Baseline Profile Compilation Status using [ProfileVerifier].
 * 使用ProfileVerifier记录应用程序的基线配置文件编译状态。
 *
 * When delivering through Google Play, the baseline profile is compiled during installation.
 * In this case you will see the correct state logged without any further action necessary.
 * To verify baseline profile installation locally, you need to manually trigger baseline
 * profile installation.
 *
 * 当通过Google Play交付时，基线配置文件在安装过程中编译。在这种情况下，您将看到正确的状态记录，而不需要任何进一步的操作。
 * 要在本地验证基线配置文件安装，您需要手动触发基线配置文件安装。
 * For immediate compilation, call:
 * 要立即编译，请调用:
 * ```bash
 * adb shell cmd package compile -f -m speed-profile com.example.macrobenchmark.target
 * ```
 * You can also trigger background optimizations:
 * 你也可以触发后台优化:
 * ```bash
 * adb shell pm bg-dexopt-job
 * ```
 * Both jobs run asynchronously and might take some time complete.
 * 这两个作业都异步运行，可能需要一些时间才能完成。
 *
 * To see quick turnaround of the ProfileVerifier, we recommend using `speed-profile`.
 * If you don't do either of these steps, you might only see the profile status reported as
 * "enqueued for compilation" when running the sample locally.
 *
 * 要查看ProfileVerifier的快速周转，我们建议使用speed-profile。如果您不执行这两个步骤中的任何一个，那么在本地运行示例时，您可能只会看到概要文件状态报告为“排队等待编译”。
 * @see androidx.profileinstaller.ProfileVerifier.CompilationStatus.ResultCode
 */
// 配置文件验证程序记录器（profileInstaller相关）
class ProfileVerifierLogger @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
) {
    companion object {
        private const val TAG = "ProfileInstaller"
    }

    operator fun invoke() = scope.launch {
        val status = ProfileVerifier.getCompilationStatusAsync().await()
        Log.d(TAG, "Status code: ${status.profileInstallResultCode}")
        Log.d(
            TAG,
            when {
                // 使用配置文件编译的App
                status.isCompiledWithProfile -> "App compiled with profile"
                // 排队等待编译的概要文件
                status.hasProfileEnqueuedForCompilation() -> "Profile enqueued for compilation"
                // 未编译或未排队的概要文件
                else -> "Profile not compiled nor enqueued"
            },
        )
    }
}
