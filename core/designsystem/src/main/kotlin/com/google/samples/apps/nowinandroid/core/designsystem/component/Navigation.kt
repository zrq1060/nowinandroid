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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android navigation bar item with icon and label content slots. Wraps Material 3
 * [NavigationBarItem].
 * Now in Android导航栏item，带有图标和标签内容槽。Wraps Material 3 [NavigationBarItem].
 *
 * @param selected Whether this item is selected.
 *                  是否选中该项。
 * @param onClick The callback to be invoked when this item is selected.
 *                  选择该项时要调用的回调。
 * @param icon The item icon content.
 *                  项目图标内容。
 * @param modifier Modifier to be applied to this item.
 *                  应用于该项的修饰符。
 * @param selectedIcon The item icon content when selected.
 *                  被选中时的项目图标内容。
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制该项的启用状态。当为false时，此项目将不可单击，并且对可访问性服务显示为禁用。
 * @param label The item text label content.
 *                  项目文本标签内容。
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 *                  是否总是显示该项的标签。如果为false，则只有在选择该项时才会显示标签。
 */
@Composable
// 导航BarItem（用于底部导航栏），用于NavigationBar使用。
fun RowScope.NiaNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        // 颜色
        colors = NavigationBarItemDefaults.colors(
            // 图标
            selectedIconColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = NiaNavigationDefaults.navigationContentColor(),
            // 文本
            selectedTextColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = NiaNavigationDefaults.navigationContentColor(),
            // 指示器
            indicatorColor = NiaNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * Now in Android navigation bar with content slot. Wraps Material 3 [NavigationBar].
 * Now in Android 导航栏与内容槽。Wraps Material 3 [NavigationBar].
 *
 * @param modifier Modifier to be applied to the navigation bar.
 *                  应用于导航栏的修饰符。
 * @param content Destinations inside the navigation bar. This should contain multiple
 * [NavigationBarItem]s.
 *                  导航栏内的目的地。这应该包含多个[NavigationBarItem]。
 */
@Composable
// 导航Bar（用于底部导航栏），包含多个[NavigationBarItem]。
fun NiaNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = NiaNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}

/**
 * Now in Android navigation rail item with icon and label content slots. Wraps Material 3
 * [NavigationRailItem].
 * Now in Android导航栏项目与图标和标签内容槽。Wraps Material 3 [NavigationRailItem].
 *
 * @param selected Whether this item is selected.
 *                  是否选中该项。
 * @param onClick The callback to be invoked when this item is selected.
 *                  选择该项时要调用的回调。
 * @param icon The item icon content.
 *                  项目图标内容。
 * @param modifier Modifier to be applied to this item.
 *                  应用于该项的修饰符。
 * @param selectedIcon The item icon content when selected.
 *                  被选中时的项目图标内容。
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 *                  控制该项的启用状态。当为false时，此项目将不可单击，并且对可访问性服务显示为禁用。
 * @param label The item text label content.
 *                  项目文本标签内容。
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 *                  是否总是显示该项的标签。如果为false，则只有在选择该项时才会显示标签。
 */
@Composable
// 导航Rail（轨道）Item（用于侧边导航栏，侧边导航栏通常在大屏幕设备上使用），用于NavigationRail使用。
fun NiaNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        // 颜色
        colors = NavigationRailItemDefaults.colors(
            // 图标
            selectedIconColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = NiaNavigationDefaults.navigationContentColor(),
            // 文本
            selectedTextColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = NiaNavigationDefaults.navigationContentColor(),
            // 指示器
            indicatorColor = NiaNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * Now in Android navigation rail with header and content slots. Wraps Material 3 [NavigationRail].
 * Now in Android 导航栏与标题和内容槽。Wraps Material 3 [NavigationRail]。
 *
 * @param modifier Modifier to be applied to the navigation rail.
 *                  导航栏的修饰符。
 * @param header Optional header that may hold a floating action button or a logo.
 *                  可选的header，可以容纳浮动的动作按钮或徽标。
 * @param content Destinations inside the navigation rail. This should contain multiple
 * [NavigationRailItem]s.
 *                   导航栏内的目的地。这应该包含多个NavigationRailItem。
 */
@Composable
// 导航Rail（用于侧边导航栏，侧边导航栏通常在大屏幕设备上使用），包含多个[NavigationRailItem]。
fun NiaNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = NiaNavigationDefaults.navigationContentColor(),
        header = header,
        content = content,
    )
}

@ThemePreviews
@Composable
// ThemePreview两个样式（亮和暗），底部导航栏（3个）的效果。
fun NiaNavigationBarPreview() {
    val items = listOf("For you", "Saved", "Interests")
    val icons = listOf(
        NiaIcons.UpcomingBorder,
        NiaIcons.BookmarksBorder,
        NiaIcons.Grid3x3,
    )
    val selectedIcons = listOf(
        NiaIcons.Upcoming,
        NiaIcons.Bookmarks,
        NiaIcons.Grid3x3,
    )

    NiaTheme {
        NiaNavigationBar {
            items.forEachIndexed { index, item ->
                NiaNavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun NiaNavigationRailPreview() {
    val items = listOf("For you", "Saved", "Interests")
    val icons = listOf(
        NiaIcons.UpcomingBorder,
        NiaIcons.BookmarksBorder,
        NiaIcons.Grid3x3,
    )
    val selectedIcons = listOf(
        NiaIcons.Upcoming,
        NiaIcons.Bookmarks,
        NiaIcons.Grid3x3,
    )

    NiaTheme {
        NiaNavigationRail {
            items.forEachIndexed { index, item ->
                NiaNavigationRailItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

/**
 * Now in Android navigation default values.
 * Now in Android 导航默认值。
 */
object NiaNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}
