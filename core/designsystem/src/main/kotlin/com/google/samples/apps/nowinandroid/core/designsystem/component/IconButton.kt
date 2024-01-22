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

import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android toggle button with icon and checked icon content slots. Wraps Material 3
 * [IconButton].
 * Now in Android 切换按钮与图标和选中的图标内容槽。Wraps Material 3 [IconButton]。
 *
 * @param checked Whether the toggle button is currently checked.
 *                  切换按钮当前是否被选中。
 * @param onCheckedChange Called when the user clicks the toggle button and toggles checked.
 *                  当用户单击切换按钮并切换复选时调用。
 * @param modifier Modifier to be applied to the toggle button.
 *                  要应用于切换按钮的修饰符。
 * @param enabled Controls the enabled state of the toggle button. When `false`, this toggle button
 * will not be clickable and will appear disabled to accessibility services.
 *                  控制切换按钮的启用状态。当为false时，此切换按钮将不可点击，并且对可访问性服务将显示为禁用。
 * @param icon The icon content to show when unchecked.
 *                  未选中时显示的图标内容。
 * @param checkedIcon The icon content to show when checked.
 *                  选中时显示的图标内容。
 */
@Composable
// Icon切换按钮
fun NiaIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
    checkedIcon: @Composable () -> Unit = icon,
) {
    // TODO: File bug
    // Can't use regular IconToggleButton as it doesn't include a shape (appears square)
    // 不能使用常规的IconToggleButton，因为它不包含形状(显示为正方形)
    FilledIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        // 颜色
        colors = IconButtonDefaults.iconToggleButtonColors(
            // 选中的容器颜色
            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            // 选中的内容颜色
            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            // 不可用的容器颜色
            disabledContainerColor = if (checked) {
                // 不可用，并且是选中的容器颜色，透明度为0.12f。
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = NiaIconButtonDefaults.DISABLED_ICON_BUTTON_CONTAINER_ALPHA,
                )
            } else {
                // 不可用，并且是未选中的容器颜色，颜色为全透明。
                Color.Transparent
            },
        ),
    ) {
        if (checked) checkedIcon() else icon()
    }
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），IconButton选中的效果。
fun IconButtonPreview() {
    NiaTheme {
        NiaIconToggleButton(
            checked = true,
            onCheckedChange = { },
            icon = {
                Icon(
                    imageVector = NiaIcons.BookmarkBorder,
                    contentDescription = null,
                )
            },
            checkedIcon = {
                Icon(
                    imageVector = NiaIcons.Bookmark,
                    contentDescription = null,
                )
            },
        )
    }
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），IconButton未选中的效果。
fun IconButtonPreviewUnchecked() {
    NiaTheme {
        NiaIconToggleButton(
            checked = false,
            onCheckedChange = { },
            icon = {
                Icon(
                    imageVector = NiaIcons.BookmarkBorder,
                    contentDescription = null,
                )
            },
            checkedIcon = {
                Icon(
                    imageVector = NiaIcons.Bookmark,
                    contentDescription = null,
                )
            },
        )
    }
}

/**
 * Now in Android icon button default values.
 * Now in Android icon 按钮默认值。
 */
object NiaIconButtonDefaults {
    // TODO: File bug
    // IconToggleButton disabled container alpha not exposed by IconButtonDefaults
    // IconToggleButton禁用了未由IconButtonDefaults暴露的容器alpha
    const val DISABLED_ICON_BUTTON_CONTAINER_ALPHA = 0.12f
}
