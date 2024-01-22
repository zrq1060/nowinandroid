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

package com.google.samples.apps.nowinandroid.core.ui

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalView
import androidx.metrics.performance.PerformanceMetricsState
import androidx.metrics.performance.PerformanceMetricsState.Holder
import kotlinx.coroutines.CoroutineScope

// Jank（不可靠的；多余的）统计扩展

/**
 * Retrieves [PerformanceMetricsState.Holder] from current [LocalView] and
 * remembers it until the View changes.
 * 从当前的[LocalView]中检索[PerformanceMetricsState.Holder]并记住它，直到View改变。
 * @see PerformanceMetricsState.getHolderForHierarchy
 */
@Composable
// 记住指标状态Holder
fun rememberMetricsStateHolder(): Holder {
    val localView = LocalView.current

    return remember(localView) {
        // performance性能库提供
        PerformanceMetricsState.getHolderForHierarchy(localView)
    }
}

/**
 * Convenience function to work with [PerformanceMetricsState] state. The side effect is
 * re-launched if any of the [keys] value is not equal to the previous composition.
 * 使用[PerformanceMetricsState]状态的便利函数。如果任何[keys]值不等于前一个组合，则会重新启动副作用。
 * @see TrackDisposableJank if you need to work with DisposableEffect to cleanup added state.
 * @see TrackDisposableJank 如果您需要使用DisposableEffect来清除添加的状态。
 */
@Composable
// 跟踪Jank
fun TrackJank(
    vararg keys: Any,
    reportMetric: suspend CoroutineScope.(state: Holder) -> Unit,
) {
    // 指标
    val metrics = rememberMetricsStateHolder()
    // LaunchedEffect 是一个用于处理启动效果（side effect）的 Compose 函数。它用于执行在 Compose 组件的生命周期内只执行一次的异步操作。
    // 这通常用于启动异步任务，比如在组件第一次创建时执行某些操作，例如订阅数据流、网络请求或执行后台任务。
    LaunchedEffect(metrics, *keys) {
        // 执行启动效果操作，例如异步任务
        // 注意: 这里不能更新 Compose 界面的状态
        // key1, key2, ... 是依赖项的键，当这些键发生变化时，LaunchedEffect 的效果会重新启动。如果没有提供键，则 LaunchedEffect 只会在组件的第一次创建时执行。
        reportMetric(metrics)
    }
}

/**
 * Convenience function to work with [PerformanceMetricsState] state that needs to be cleaned up.
 * The side effect is re-launched if any of the [keys] value is not equal to the previous composition.
 * 用于处理需要清理的[PerformanceMetricsState]状态的便利函数。如果任何[keys]值不等于前一个组合，则会重新启动副作用。
 */
@Composable
fun TrackDisposableJank(
    vararg keys: Any,
    reportMetric: DisposableEffectScope.(state: Holder) -> DisposableEffectResult,
) {
    val metrics = rememberMetricsStateHolder()
    DisposableEffect(metrics, *keys) {
        reportMetric(this, metrics)
    }
}

/**
 * Track jank while scrolling anything that's scrollable.
 * 在滚动任何可滚动的东西时跟踪jank。
 */
@Composable
fun TrackScrollJank(scrollableState: ScrollableState, stateName: String) {
    TrackJank(scrollableState) { metricsHolder ->
        snapshotFlow { scrollableState.isScrollInProgress }.collect { isScrollInProgress ->
            metricsHolder.state?.apply {
                if (isScrollInProgress) {
                    putState(stateName, "Scrolling=true")
                } else {
                    removeState(stateName)
                }
            }
        }
    }
}
