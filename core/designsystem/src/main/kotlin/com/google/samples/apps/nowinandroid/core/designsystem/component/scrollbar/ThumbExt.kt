/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

/**
 * Remembers a function to react to [Scrollbar] thumb position displacements for a [LazyListState]
 * 记住一个函数来对[LazyListState]的[Scrollbar]拇指位置位移做出反应
 * @param itemsAvailable the amount of items in the list.
 *                          list中项目的数量。
 */
@Composable
fun LazyListState.rememberDraggableScroller(
    itemsAvailable: Int,
): (Float) -> Unit = rememberDraggableScroller(
    itemsAvailable = itemsAvailable,
    scroll = ::scrollToItem,
)

/**
 * Remembers a function to react to [Scrollbar] thumb position displacements for a [LazyGridState]
 * 记住一个函数来对[LazyGridState]的[Scrollbar]拇指位置位移做出反应
 * @param itemsAvailable the amount of items in the grid.
 *                          grid中项目的数量。
 */
@Composable
fun LazyGridState.rememberDraggableScroller(
    itemsAvailable: Int,
): (Float) -> Unit = rememberDraggableScroller(
    itemsAvailable = itemsAvailable,
    scroll = ::scrollToItem,
)

/**
 * Remembers a function to react to [Scrollbar] thumb position displacements for a
 * [LazyStaggeredGridState]
 * 记住一个函数来对[LazyStaggeredGridState]的[Scrollbar]拇指位置位移做出反应
 * @param itemsAvailable the amount of items in the staggered grid.
 *                          staggered grid中项目的数量。
 */
@Composable
fun LazyStaggeredGridState.rememberDraggableScroller(
    itemsAvailable: Int,
): (Float) -> Unit = rememberDraggableScroller(
    itemsAvailable = itemsAvailable,
    scroll = ::scrollToItem,
)

/**
 * Generic function to react to [Scrollbar] thumb displacements in a lazy layout.
 * 在lazy布局中对[Scrollbar]拇指位移作出反应的通用函数。
 * @param itemsAvailable the total amount of items available to scroll in the layout.
 *                      可在布局中滚动的项目总数。
 * @param scroll a function to be invoked when an index has been identified to scroll to.
 *                      当确定要滚动到的索引时调用的函数。
 */
@Composable
private inline fun rememberDraggableScroller(
    itemsAvailable: Int,
    crossinline scroll: suspend (index: Int) -> Unit,
): (Float) -> Unit {
    // 百分比
    var percentage by remember { mutableFloatStateOf(Float.NaN) }
    // item数量
    val itemCount by rememberUpdatedState(itemsAvailable)

    LaunchedEffect(percentage) {
        // 百分比为默认值，直接返回。
        if (percentage.isNaN()) return@LaunchedEffect
        // 计算滚动的位置，总数*百分比。
        val indexToFind = (itemCount * percentage).roundToInt()
        // 滚动
        scroll(indexToFind)
    }
    return remember {
        { newPercentage -> percentage = newPercentage }
    }
}
