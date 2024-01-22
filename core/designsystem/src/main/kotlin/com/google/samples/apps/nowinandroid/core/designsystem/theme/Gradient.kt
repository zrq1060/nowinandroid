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

/**
 * A class to model gradient color values for Now in Android.
 * 一个为Now in Android建模渐变颜色值的类
 *
 * @param top The top gradient color to be rendered.
 *             要渲染的顶部渐变颜色
 * @param bottom The bottom gradient color to be rendered.
 *              要渲染的底部渐变颜色
 * @param container The container gradient color over which the gradient will be rendered.
 *              将在其上渲染渐变的容器渐变颜色
 */
@Immutable
// 不可变类，渐变颜色。
data class GradientColors(
    val top: Color = Color.Unspecified,
    val bottom: Color = Color.Unspecified,
    val container: Color = Color.Unspecified,
)

/**
 * A composition local for [GradientColors].
 * [GradientColors]的局部组合。
 */
val LocalGradientColors = staticCompositionLocalOf { GradientColors() }
