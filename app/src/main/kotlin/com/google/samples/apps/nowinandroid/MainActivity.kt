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

package com.google.samples.apps.nowinandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import com.google.samples.apps.nowinandroid.MainActivityUiState.Loading
import com.google.samples.apps.nowinandroid.MainActivityUiState.Success
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.analytics.LocalAnalyticsHelper
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.util.NetworkMonitor
import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneMonitor
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.ui.LocalTimeZone
import com.google.samples.apps.nowinandroid.ui.NiaApp
import com.google.samples.apps.nowinandroid.ui.rememberNiaAppState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
// 首页
class MainActivity : ComponentActivity() {

    /**
     * Lazily inject [JankStats], which is used to track jank throughout the app.
     * 惰性注入[JankStats]，用于在整个应用程序中跟踪jank。
     */
    @Inject
    // 该类用于积累和报告有关应用程序中UI“jank”(运行时性能问题)的信息。
    lateinit var lazyStats: dagger.Lazy<JankStats>

    @Inject
    // 网络监控，用于监听无网络。
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    // 时区监视器
    lateinit var timeZoneMonitor: TimeZoneMonitor

    @Inject
    // 分析辅助类，用于处理事件（如：打印日至等）。
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    // 用户新闻资源库
    lateinit var userNewsResourceRepository: UserNewsResourceRepository

    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // splash初始化
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // 当前页面的UI状态，默认为加载中。
        var uiState: MainActivityUiState by mutableStateOf(Loading)

        // Update the uiState
        // 更新uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        // Keep the splash screen on-screen until the UI state is loaded. This condition is
        // evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
        // the UI.
        // 在UI状态加载完成之前，保持启动画面在屏幕上。每次需要重新绘制应用程序时，都会评估此条件，因此它应该很快，以避免阻塞UI。
        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                Loading -> true
                is Success -> false
            }
        }

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations, and go edge-to-edge
        // This also sets up the initial system bar style based on the platform theme
        // 关闭装饰配件系统窗口，它允许我们处理插页，包括IME动画，并边缘到边缘
        // 根据平台主题设置初始的系统栏样式
        enableEdgeToEdge()

        setContent {
            // 是否是暗主题
            val darkTheme = shouldUseDarkTheme(uiState)

            // Update the edge to edge configuration to match the theme
            // This is the same parameters as the default enableEdgeToEdge call, but we manually
            // resolve whether or not to show dark theme using uiState, since it can be different
            // than the configuration's dark theme value based on the user preference.
            // 更新边缘到边缘的配置以匹配主题
            // 这是与默认的enableEdgeToEdge调用相同的参数，但是我们使用uiState手动解决是否显示暗主题，因为它可以与基于用户首选项的配置的暗主题值不同。
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }

            val appState = rememberNiaAppState(
                windowSizeClass = calculateWindowSizeClass(this),
                networkMonitor = networkMonitor,
                userNewsResourceRepository = userNewsResourceRepository,
                timeZoneMonitor = timeZoneMonitor,
            )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
                LocalTimeZone provides currentTimeZone,
            ) {
                NiaTheme(
                    // 暗模式
                    darkTheme = darkTheme,
                    // android样式
                    androidTheme = shouldUseAndroidTheme(uiState),
                    // 禁用动态样式
                    disableDynamicTheming = shouldDisableDynamicTheming(uiState),
                ) {
                    NiaApp(appState)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lazyStats.get().isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        lazyStats.get().isTrackingEnabled = false
    }
}

/**
 * Returns `true` if the Android theme should be used, as a function of the [uiState].
 * 如果要使用Android主题，返回' true '，作为[uiState]的一个函数。
 */
@Composable
// 是否用android样式，加载中状态不用，成功状态用用户自己设置的。
private fun shouldUseAndroidTheme(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    Loading -> false
    is Success -> when (uiState.userData.themeBrand) {
        ThemeBrand.DEFAULT -> false
        ThemeBrand.ANDROID -> true
    }
}

/**
 * Returns `true` if the dynamic color is disabled, as a function of the [uiState].
 * 如果动态颜色被禁用，返回true，作为[uiState]的一个函数。
 */
@Composable
// 是否禁用动态模式，加载中状态不禁用，成功状态用用户自己设置的。
private fun shouldDisableDynamicTheming(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    Loading -> false
    is Success -> !uiState.userData.useDynamicColor
}

/**
 * Returns `true` if dark theme should be used, as a function of the [uiState] and the
 * current system context.
 * 如果应该使用dark主题，作为[uiState]和当前系统上下文的函数，则返回true。
 */
@Composable
// 是否用暗模式，加载中状态用系统动态默认的，成功状态用用户自己设置的。
private fun shouldUseDarkTheme(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    Loading -> isSystemInDarkTheme()
    is Success -> when (uiState.userData.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * 由androidx和平台定义的默认 light scrim：
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * 由androidx和平台定义的默认 dark scrim：
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)
