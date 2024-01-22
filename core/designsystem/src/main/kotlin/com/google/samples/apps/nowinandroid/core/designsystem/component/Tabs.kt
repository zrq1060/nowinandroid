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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android tab. Wraps Material 3 [Tab] and shifts text label down.
 * Now in Android tab。Wraps Material 3 [Tab] 并向下移动文本标签。
 *
 * @param selected Whether this tab is selected or not.
 *                  是否选中此选项卡。
 * @param onClick The callback to be invoked when this tab is selected.
 *                  选择此选项卡时要调用的回调。
 * @param modifier Modifier to be applied to the tab.
 *                  要应用于选项卡的修饰符。
 * @param enabled Controls the enabled state of the tab. When `false`, this tab will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制选项卡的启用状态。当为false时，此选项卡将不可点击，并且对可访问性服务显示为禁用。
 * @param text The text label content.
 *                  文本标签内容。
 */
@Composable
// Tab，用于放入到TabRow中。
fun NiaTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        text = {
            val style = MaterialTheme.typography.labelLarge.copy(textAlign = TextAlign.Center)
            // 提供文本样式，排版：大标签
            ProvideTextStyle(
                value = style,
                content = {
                    // 内容设置paddingTop=7dp，实现向下移动文本标签效果。
                    Box(modifier = Modifier.padding(top = NiaTabDefaults.TabTopPadding)) {
                        text()
                    }
                },
            )
        },
    )
}

/**
 * Now in Android tab row. Wraps Material 3 [TabRow].
 * Now in Android 选项卡行。Wraps Material 3 [TabRow]。
 *
 * @param selectedTabIndex The index of the currently selected tab.
 *                          当前选中的选项卡的索引。
 * @param modifier Modifier to be applied to the tab row.
 *                  要应用于选项卡行的修饰符。
 * @param tabs The tabs inside this tab row. Typically this will be multiple [NiaTab]s. Each element
 * inside this lambda will be measured and placed evenly across the row, each taking up equal space.
 *              此选项卡行的选项卡。通常会有多个[NiaTab]。这个lambda中的每个元素将被测量并均匀地放置在行中，每个元素占用相同的空间。
 */
@Composable
fun NiaTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit,
) {
    TabRow(
        // 选中的tab位置
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        // 容器颜色
        containerColor = Color.Transparent,
        // 内容颜色
        contentColor = MaterialTheme.colorScheme.onSurface,
        // 指示器
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                // 指示器偏移为选中的tab位置的距离
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                height = 2.dp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        tabs = tabs,
    )
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），两个Tab的效果。
fun TabsPreview() {
    NiaTheme {
        val titles = listOf("Topics", "People")
        NiaTabRow(selectedTabIndex = 0) {
            titles.forEachIndexed { index, title ->
                NiaTab(
                    selected = index == 0,
                    onClick = { },
                    text = { Text(text = title) },
                )
            }
        }
    }
}

object NiaTabDefaults {
    val TabTopPadding = 7.dp
}
