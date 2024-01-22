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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android view toggle button with included trailing icon as well as compact and expanded
 * text label content slots.
 * Now in Android view视图切换按钮，包括尾随图标以及紧凑和扩展的文本标签内容槽。
 *
 * @param expanded Whether the view toggle is currently in expanded mode or compact mode.
 *                  view切换开关当前是处于展开模式还是压缩模式。
 * @param onExpandedChange Called when the user clicks the button and toggles the mode.
 *                  当用户单击按钮并切换模式时调用。
 * @param modifier Modifier to be applied to the button.
 *                  应用于按钮的修饰符。
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制按钮的启用状态。当为false时，此按钮将不可点击，并且对可访问性服务显示为禁用。
 * @param compactText The text label content to show in expanded mode.
 *                  以展开模式显示的文本标签内容。
 * @param expandedText The text label content to show in compact mode.
 *                  以紧凑模式显示的文本标签内容。
 */
@Composable
// View切换按钮
fun NiaViewToggleButton(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    compactText: @Composable () -> Unit,
    expandedText: @Composable () -> Unit,
) {
    // 文本按钮
    TextButton(
        // 点击，通知展开的改变。
        onClick = { onExpandedChange(!expanded) },
        modifier = modifier,
        enabled = enabled,
        // 颜色
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        // Padding
        contentPadding = NiaViewToggleDefaults.ViewToggleButtonContentPadding,
    ) {
        // 上面设置的是按钮的状态、点击效果之类的，下面设置的是文本按钮内部布局。
        // 切换按钮
        NiaViewToggleButtonContent(
            text = if (expanded) expandedText else compactText,
            // 后面的图标
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) NiaIcons.ViewDay else NiaIcons.ShortText,
                    contentDescription = null,
                )
            },
        )
    }
}

/**
 * Internal Now in Android view toggle button content layout for arranging the text label and
 * trailing icon.
 * 内部Now in Android view视图切换按钮内容布局，用于安排文本标签和尾随图标。
 *
 * @param text The button text label content.
 *              按钮文本标签内容。
 * @param trailingIcon The button trailing icon content. Default is `null` for no trailing icon.
 *                      按钮尾部的图标内容。如果没有尾随图标，默认为null。
 */
@Composable
// 切换按钮内容
private fun NiaViewToggleButtonContent(
    // 文本
    text: @Composable () -> Unit,
    // 尾随图标
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    // 文本Box
    Box(
        Modifier
            .padding(
                // 有尾随图标，设置paddingEnd。
                end = if (trailingIcon != null) {
                    ButtonDefaults.IconSpacing
                } else {
                    0.dp
                },
            ),
    ) {
        // 设置文本样式，排版：小标签。
        ProvideTextStyle(value = MaterialTheme.typography.labelSmall) {
            text()
        }
    }
    // 尾随图标Box
    if (trailingIcon != null) {
        Box(Modifier.sizeIn(maxHeight = ButtonDefaults.IconSize)) {
            trailingIcon()
        }
    }
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），按钮是展开的。
fun ViewTogglePreviewExpanded() {
    NiaTheme {
        Surface {
            NiaViewToggleButton(
                expanded = true,
                onExpandedChange = { },
                compactText = { Text(text = "Compact view") },
                expandedText = { Text(text = "Expanded view") },
            )
        }
    }
}

@Preview
@Composable
// 一个样式，按钮是紧凑的。
fun ViewTogglePreviewCompact() {
    NiaTheme {
        Surface {
            NiaViewToggleButton(
                expanded = false,
                onExpandedChange = { },
                compactText = { Text(text = "Compact view") },
                expandedText = { Text(text = "Expanded view") },
            )
        }
    }
}

/**
 * Now in Android view toggle default values.
 * Now in Android view切换默认值。
 */
object NiaViewToggleDefaults {
    // TODO: File bug
    // Various default button padding values aren't exposed via ButtonDefaults
    // 各种默认的按钮填充值不会通过ButtonDefaults公开
    val ViewToggleButtonContentPadding =
        PaddingValues(
            start = 16.dp,
            top = 8.dp,
            end = 12.dp,
            bottom = 8.dp,
        )
}
