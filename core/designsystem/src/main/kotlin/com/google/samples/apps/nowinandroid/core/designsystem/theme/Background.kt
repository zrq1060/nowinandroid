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

package com.google.samples.apps.nowinandroid.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * A class to model background color and tonal elevation values for Now in Android.
 * 为Now in Android建模背景颜色和色调海拔的类
 */
@Immutable
// 不可变类，背景样式。
data class BackgroundTheme(
    // 背景颜色，为未定义（透明色）。
    val color: Color = Color.Unspecified,
    // 背景色调海拔，为未定义。
    val tonalElevation: Dp = Dp.Unspecified,
)

/**
 * A composition local for [BackgroundTheme].
 * [BackgroundTheme]的局部组合。
 */
val LocalBackgroundTheme = staticCompositionLocalOf { BackgroundTheme() }
