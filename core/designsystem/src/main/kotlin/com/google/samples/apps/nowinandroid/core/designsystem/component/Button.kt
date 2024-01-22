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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android filled button with generic content slot. Wraps Material 3 [Button].
 * Now in Android 填充按钮与通用内容槽。Wraps Material 3 [Button]。
 *
 * @param onClick Will be called when the user clicks the button.
 *                  当用户单击按钮时调用。
 * @param modifier Modifier to be applied to the button.
 *                  应用于按钮的修饰符。
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制按钮的启用状态。当为false时，此按钮将不可点击，并且对可访问性服务显示为禁用。
 * @param contentPadding The spacing values to apply internally between the container and the
 * content.
 *                  在容器和内容之间应用的内部间距值。
 * @param content The button content.
 *                  按钮内容。
 */
@Composable
// 普通按钮（具有填充、无边框），无图标。
fun NiaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
        ),
        contentPadding = contentPadding,
        content = content,
    )
}

/**
 * Now in Android filled button with text and icon content slots.
 * Now in Android填充按钮与文本和图标内容槽。
 *
 * @param onClick Will be called when the user clicks the button.
 *                  当用户单击按钮时调用。
 * @param modifier Modifier to be applied to the button.
 *                  应用于按钮的修饰符。
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制按钮的启用状态。当为false时，此按钮将不可点击，并且对可访问性服务显示为禁用。
 * @param text The button text label content.
 *                  按钮文本标签内容。
 * @param leadingIcon The button leading icon content. Pass `null` here for no leading icon.
 *                  按钮引导图标内容。如果没有前导图标，此处传递null。
 */
@Composable
// 普通按钮（具有填充、无边框），有前图标。
fun NiaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    NiaButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        },
    ) {
        // 图标+文本按钮
        NiaButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Now in Android outlined button with generic content slot. Wraps Material 3 [OutlinedButton].
 * Now in Android 概述按钮与通用内容槽。Wraps Material 3 [OutlinedButton]。
 *
 * @param onClick Will be called when the user clicks the button.
 *                  当用户单击按钮时调用。
 * @param modifier Modifier to be applied to the button.
 *                  应用于按钮的修饰符。
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制按钮的启用状态。当为false时，此按钮将不可点击，并且对可访问性服务显示为禁用。
 * @param contentPadding The spacing values to apply internally between the container and the
 * content.
 *                  在容器和内容之间应用的内部间距值。
 * @param content The button content.
 *                  按钮内容。
 */
@Composable
// Outlined按钮（无填充、有边框），无图标。
fun NiaOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        border = BorderStroke(
            width = NiaButtonDefaults.OutlinedButtonBorderWidth,
            color = if (enabled) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = NiaButtonDefaults.DISABLED_OUTLINED_BUTTON_BORDER_ALPHA,
                )
            },
        ),
        contentPadding = contentPadding,
        content = content,
    )
}

/**
 * Now in Android outlined button with text and icon content slots.
 * Now in Android概述按钮与文本和图标内容槽。
 *
 * @param onClick Will be called when the user clicks the button.
 *                  当用户单击按钮时调用。
 * @param modifier Modifier to be applied to the button.
 *                  应用于按钮的修饰符。
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制按钮的启用状态。当为false时，此按钮将不可点击，并且对可访问性服务显示为禁用。
 * @param text The button text label content.
 *                  按钮文本标签内容。
 * @param leadingIcon The button leading icon content. Pass `null` here for no leading icon.
 *                  按钮引导图标内容。如果没有前导图标，此处传递null。
 */
@Composable
// Outlined按钮（无填充、有边框），有前图标。
fun NiaOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    NiaOutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        },
    ) {
        NiaButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Now in Android text button with generic content slot. Wraps Material 3 [TextButton].
 * Now in Android 文本按钮与通用内容槽。Wraps Material 3 [TextButton].
 *
 * @param onClick Will be called when the user clicks the button.
 *                  当用户单击按钮时调用。
 * @param modifier Modifier to be applied to the button.
 *                  应用于按钮的修饰符。
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制按钮的启用状态。当为false时，此按钮将不可点击，并且对可访问性服务显示为禁用。
 * @param content The button content.
 *                  按钮内容。
 */
@Composable
// 文本按钮（无填充、无边框），无图标。
fun NiaTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        content = content,
    )
}

/**
 * Now in Android text button with text and icon content slots.
 * Now in Android 文本按钮与文本和图标内容槽。
 *
 * @param onClick Will be called when the user clicks the button.
 *                  当用户单击按钮时调用。
 * @param modifier Modifier to be applied to the button.
 *                  应用于按钮的修饰符。
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制按钮的启用状态。当为false时，此按钮将不可点击，并且对可访问性服务显示为禁用。
 * @param text The button text label content.
 *                  按钮文本标签内容。
 * @param leadingIcon The button leading icon content. Pass `null` here for no leading icon.
 *                  按钮引导图标内容。如果没有前导图标，此处传递null。
 */
@Composable
// 文本按钮（无填充、无边框），有前图标。
fun NiaTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    NiaTextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        NiaButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Internal Now in Android button content layout for arranging the text label and leading icon.
 * 内部Now in Android按钮内容布局安排文本标签和领先的图标。
 *
 * @param text The button text label content.
 *                  按钮文本标签内容。
 * @param leadingIcon The button leading icon content. Default is `null` for no leading icon.Ï
 *                  按钮引导图标内容。默认为null，没有先导icon.Ï
 */
@Composable
// 前图标+文本按钮，内部使用。
private fun NiaButtonContent(
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    // 前图标
    if (leadingIcon != null) {
        Box(Modifier.sizeIn(maxHeight = ButtonDefaults.IconSize)) {
            leadingIcon()
        }
    }
    // 文本
    Box(
        Modifier
            .padding(
                start = if (leadingIcon != null) {
                    ButtonDefaults.IconSpacing
                } else {
                    0.dp
                },
            ),
    ) {
        text()
    }
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），普通按钮-无前图标-可用状态下的效果。
fun NiaButtonPreview() {
    NiaTheme {
        NiaBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            NiaButton(onClick = {}, text = { Text("Test button") })
        }
    }
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），Outlined按钮-无前图标-可用状态下的效果。
fun NiaOutlinedButtonPreview() {
    NiaTheme {
        NiaBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            NiaOutlinedButton(onClick = {}, text = { Text("Test button") })
        }
    }
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），普通按钮-无前图标-可用状态下的效果。
fun NiaButtonPreview2() {
    NiaTheme {
        NiaBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            NiaButton(onClick = {}, text = { Text("Test button") })
        }
    }
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），普通按钮-有前图标-可用状态下的效果。
fun NiaButtonLeadingIconPreview() {
    NiaTheme {
        NiaBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            NiaButton(
                onClick = {},
                text = { Text("Test button") },
                leadingIcon = { Icon(imageVector = NiaIcons.Add, contentDescription = null) },
            )
        }
    }
}

/**
 * Now in Android button default values.
 * Now in Android 按钮默认值。
 */
object NiaButtonDefaults {
    // TODO: File bug
    // OutlinedButton border color doesn't respect disabled state by default
    // OutlinedButton边框颜色默认情况下不尊重禁用状态
    const val DISABLED_OUTLINED_BUTTON_BORDER_ALPHA = 0.12f

    // TODO: File bug
    // OutlinedButton default border width isn't exposed via ButtonDefaults
    // OutlinedButton默认边框宽度不通过ButtonDefaults暴露
    val OutlinedButtonBorderWidth = 1.dp
}
