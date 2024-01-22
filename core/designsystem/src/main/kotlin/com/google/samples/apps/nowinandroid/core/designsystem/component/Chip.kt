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

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android filter chip with included leading checked icon as well as text content slot.
 * Now in Android 筛选标签，包括领先的检查图标以及文本内容槽。
 *
 * @param selected Whether the chip is currently checked.
 *                  当前是否检查标签。
 * @param onSelectedChange Called when the user clicks the chip and toggles checked.
 *                  当用户点击标签并切换复选时调用。
 * @param modifier Modifier to be applied to the chip.
 *                  应用于标签的修饰符。
 * @param enabled Controls the enabled state of the chip. When `false`, this chip will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制标签的使能状态。当为false时，该标签将不可点击，并将显示禁用访问服务。
 * @param label The text label content.
 *                  文本标签内容。
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
// 筛选标签
fun NiaFilterChip(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        // 文本标签内容
        label = {
            // 提供文本样式，排版：小标签
            ProvideTextStyle(value = MaterialTheme.typography.labelSmall) {
                label()
            }
        },
        modifier = modifier,
        enabled = enabled,
        // 前面的图标，选中为Icon，未选中为没有。
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = NiaIcons.Check,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        shape = CircleShape,
        // 边框
        border = FilterChipDefaults.filterChipBorder(
            // 边框颜色
            borderColor = MaterialTheme.colorScheme.onBackground,
            // 选中的边框颜色
            selectedBorderColor = MaterialTheme.colorScheme.onBackground,
            // 不可用的边框颜色
            disabledBorderColor = MaterialTheme.colorScheme.onBackground.copy(
                alpha = NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            // 不可用的选中的边框颜色
            disabledSelectedBorderColor = MaterialTheme.colorScheme.onBackground.copy(
                alpha = NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            // 选中的边框宽度
            selectedBorderWidth = NiaChipDefaults.ChipBorderWidth,
        ),
        // 颜色
        colors = FilterChipDefaults.filterChipColors(
            // 可用-未选中，颜色
            // -标签颜色
            labelColor = MaterialTheme.colorScheme.onBackground,
            // -Icon颜色
            iconColor = MaterialTheme.colorScheme.onBackground,

            // 不可用-未选中，颜色
            // -不可用的容器颜色
            disabledContainerColor = if (selected) {
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = NiaChipDefaults.DISABLED_CHIP_CONTAINER_ALPHA,
                )
            } else {
                Color.Transparent
            },
            // -不可用的标签颜色
            disabledLabelColor = MaterialTheme.colorScheme.onBackground.copy(
                alpha = NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            // -不可用的前面的图标颜色
            disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground.copy(
                alpha = NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),

            // 可用-选中，颜色
            // -选中的容器颜色
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            // -选中的标签颜色
            selectedLabelColor = MaterialTheme.colorScheme.onBackground,
            // -选中的前面的图标颜色
            selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
        ),
    )
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），NiaFilterChip选中的效果。
fun ChipPreview() {
    NiaTheme {
        NiaBackground(modifier = Modifier.size(80.dp, 20.dp)) {
            NiaFilterChip(selected = true, onSelectedChange = {}) {
                Text("Chip")
            }
        }
    }
}

/**
 * Now in Android chip default values.
 * Now in Android 标签默认值。
 */
object NiaChipDefaults {
    // TODO: File bug
    // FilterChip default values aren't exposed via FilterChipDefaults
    // FilterChip默认值不会通过FilterChipDefaults暴露
    const val DISABLED_CHIP_CONTAINER_ALPHA = 0.12f
    const val DISABLED_CHIP_CONTENT_ALPHA = 0.38f
    val ChipBorderWidth = 1.dp
}
