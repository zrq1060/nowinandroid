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

package com.google.samples.apps.nowinandroid.core.designsystem.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import kotlinx.coroutines.launch

@Composable
// 加载Loading
fun NiaLoadingWheel(
    contentDesc: String,
    modifier: Modifier = Modifier,
) {
    // 无限过渡
    val infiniteTransition = rememberInfiniteTransition(label = "wheel transition")

    // Specifies the float animation for slowly drawing out the lines on entering
    // 指定在输入时缓慢绘制线条的浮动动画
    // 开始值，检查为0，其它为1。
    val startValue = if (LocalInspectionMode.current) 0F else 1F
    // float动画值集合，12条数据，默认值为1。
    val floatAnimValues = (0 until NUM_OF_LINES).map { remember { Animatable(startValue) } }
    // 启动效应
    LaunchedEffect(floatAnimValues) {
        // 遍历12次动画
        (0 until NUM_OF_LINES).map { index ->
            // 启动协程
            launch {
                // 执行一次动画，1->0，持续时间0.1s，延迟时间为 0.04s * index。
                floatAnimValues[index].animateTo(
                    targetValue = 0F,
                    // 动画规格，补间动画，持续时间0.1s，延迟时间为 40 * index。
                    animationSpec = tween(
                        durationMillis = 100,
                        easing = FastOutSlowInEasing,
                        delayMillis = 40 * index,
                    ),
                )
            }
        }
    }

    // Specifies the rotation animation of the entire Canvas composable
    // 指定整个可组合画布的旋转动画
    // 旋转动画，目标360度，无限重复，12s一圈。
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        // 动画规格，无限重复，补间动画，12s一圈。
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ROTATION_TIME, easing = LinearEasing),
        ),
        label = "wheel rotation animation",
    )

    // Specifies the color animation for the base-to-progress line color change
    // 指定从基线到进度线颜色变化的颜色动画
    // 基线的颜色，目标颜色。
    val baseLineColor = MaterialTheme.colorScheme.onBackground
    // 进度线的颜色，变化颜色。
    val progressLineColor = MaterialTheme.colorScheme.inversePrimary

    // 颜色动画值集合，12条数据，目标baseLineColor。
    val colorAnimValues = (0 until NUM_OF_LINES).map { index ->
        // 执行一个动画
        infiniteTransition.animateColor(
            initialValue = baseLineColor,
            targetValue = baseLineColor,
            // 动画规格，无限重复，关键帧动画，持续时间为6s。
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = ROTATION_TIME / 2
                    // KeyframeEntity，在0.5s时，颜色为progressLineColor，线性宽松。
                    progressLineColor at ROTATION_TIME / NUM_OF_LINES / 2 using LinearEasing
                    // KeyframeEntity，在1s时，颜色为baseLineColor，线性宽松。
                    baseLineColor at ROTATION_TIME / NUM_OF_LINES using LinearEasing
                },
                // 重复模式：重新启动
                repeatMode = RepeatMode.Restart,
                // 初始起始偏移量，为 0.5s * index。
                initialStartOffset = StartOffset(ROTATION_TIME / NUM_OF_LINES / 2 * index),
            ),
            label = "wheel color animation",
        )
    }

    // Draws out the LoadingWheel Canvas composable and sets the animations
    // 绘制可组合的LoadingWheel画布并设置动画
    Canvas(
        modifier = modifier
            .size(48.dp)
            .padding(8.dp)
            // 图形层，旋转Z轴，实现轮子转动。
            .graphicsLayer { rotationZ = rotationAnim }
            .semantics { contentDescription = contentDesc }
            .testTag("loadingWheel"),
    ) {
        // 画12条线
        repeat(NUM_OF_LINES) { index ->
            // 每一条线，旋转 index * 30 度。
            rotate(degrees = index * 30f) {
                // 画一条线
                drawLine(
                    // 线颜色
                    color = colorAnimValues[index].value,
                    // Animates the initially drawn 1 pixel alpha from 0 to 1
                    // 将最初绘制的1像素alpha从0动画到1
                    // 线透明度，默认floatAnimValues值为1，为全部透明，到floatAnimValues延迟时间后值修改，即为全不透明。
                    alpha = if (floatAnimValues[index].value < 1f) 1f else 0f,
                    // 线宽度
                    strokeWidth = 4F,
                    // 线圆角
                    cap = StrokeCap.Round,
                    // 开始位置，x为中心点，y为正上方半径的一半。
                    start = Offset(size.width / 2, size.height / 4),
                    // 结束位置，x为中心点，y为正上方半径的一半*floatAnimValues值。
                    end = Offset(size.width / 2, floatAnimValues[index].value * size.height / 4),
                )
            }
        }
    }
}

@Composable
// 覆盖加载Loading，大小为60dp。
fun NiaOverlayLoadingWheel(
    contentDesc: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(60.dp),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.83f),
        modifier = modifier
            .size(60.dp),
    ) {
        NiaLoadingWheel(
            contentDesc = contentDesc,
        )
    }
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），NiaLoadingWheel的效果。
fun NiaLoadingWheelPreview() {
    NiaTheme {
        Surface {
            NiaLoadingWheel(contentDesc = "LoadingWheel")
        }
    }
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），NiaOverlayLoadingWheel的效果。
fun NiaOverlayLoadingWheelPreview() {
    NiaTheme {
        Surface {
            NiaOverlayLoadingWheel(contentDesc = "LoadingWheel")
        }
    }
}

private const val ROTATION_TIME = 12000
private const val NUM_OF_LINES = 12
