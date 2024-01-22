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

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

@Composable
// 主题标签
fun NiaTopicTag(
    modifier: Modifier = Modifier,
    // 是否关注
    followed: Boolean,
    // 点击监听
    onClick: () -> Unit,
    // 是否可用
    enabled: Boolean = true,
    // 文本内容
    text: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        // 容器颜色
        val containerColor = if (followed) {
            // 关注的，颜色为主容器颜色。
            MaterialTheme.colorScheme.primaryContainer
        } else {
            // 没关注的，颜色为透明度为0.5的主表面变体颜色。
            MaterialTheme.colorScheme.surfaceVariant.copy(
                // 透明度为0.5
                alpha = NiaTagDefaults.UNFOLLOWED_TOPIC_TAG_CONTAINER_ALPHA,
            )
        }
        // 文本按钮
        TextButton(
            onClick = onClick,
            enabled = enabled,
            // 按钮所有颜色
            colors = ButtonDefaults.textButtonColors(
                // 容器颜色，即背景颜色。
                containerColor = containerColor,
                // 内容颜色
                contentColor = contentColorFor(backgroundColor = containerColor),
                // 不可用容器颜色
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                    // 透明度为0.12
                    alpha = NiaTagDefaults.DISABLED_TOPIC_TAG_CONTAINER_ALPHA,
                ),
            ),
        ) {
            // 提供文本样式，排版为：小标签
            ProvideTextStyle(value = MaterialTheme.typography.labelSmall) {
                text()
            }
        }
    }
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），都是关注的效果。
fun TagPreview() {
    NiaTheme {
        NiaTopicTag(followed = true, onClick = {}) {
            Text("Topic".uppercase())
        }
    }
}

/**
 * Now in Android tag default values.
 * Now in Android 标签默认值。
 */
object NiaTagDefaults {
    const val UNFOLLOWED_TOPIC_TAG_CONTAINER_ALPHA = 0.5f

    // TODO: File bug
    // Button disabled container alpha value not exposed by ButtonDefaults
    // 按钮禁用容器alpha值未由ButtonDefaults公开
    const val DISABLED_TOPIC_TAG_CONTAINER_ALPHA = 0.12f
}
