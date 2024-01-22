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

package com.google.samples.apps.nowinandroid.feature.interests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.component.DynamicAsyncImage
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaIconToggleButton
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.feature.interests.R.string

@Composable
// Interests（兴趣）Item布局
fun InterestsItem(
    name: String,
    // 是否关注
    following: Boolean,
    topicImageUrl: String,
    // item点击通知
    onClick: () -> Unit,
    // 关注/取消关注按钮点击通知
    onFollowButtonClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    // Icon的修饰符
    iconModifier: Modifier = Modifier,
    description: String = "",
) {
    ListItem(
        // 居前内容，居左，如icon。
        leadingContent = {
            InterestsIcon(topicImageUrl, iconModifier.size(64.dp))
        },
        // 标题内容，居中。
        headlineContent = {
            Text(text = name)
        },
        // 上划线内容，居上。
//        overlineContent = {
//            Text(text = "上方内容")
//        },
        // 次要内容，居下。
        supportingContent = {
            Text(text = description)
        },
        // 尾随内容，居右，如text, icon, switch or checkbox。
        trailingContent = {
            // 切换按钮
            NiaIconToggleButton(
                checked = following,
                // 改变通知
                onCheckedChange = onFollowButtonClick,
                // 普通Icon
                icon = {
                    Icon(
                        imageVector = NiaIcons.Add,
                        contentDescription = stringResource(
                            id = string.feature_interests_card_follow_button_content_desc,
                        ),
                    )
                },
                // 选中的Icon
                checkedIcon = {
                    Icon(
                        imageVector = NiaIcons.Check,
                        contentDescription = stringResource(
                            id = string.feature_interests_card_unfollow_button_content_desc,
                        ),
                    )
                },
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        // 修饰：可点击
        modifier = modifier
            .semantics(mergeDescendants = true) { /* no-op */ }
            .clickable(enabled = true, onClick = onClick),
    )
}

@Composable
// Interests（兴趣）Item布局-兴趣Icon
private fun InterestsIcon(topicImageUrl: String, modifier: Modifier = Modifier) {
    if (topicImageUrl.isEmpty()) {
        // 地址为空，显示默认图。
        Icon(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp),
            imageVector = NiaIcons.Person,
            // decorative image
            contentDescription = null,
        )
    } else {
        // 地址不为空，显示加载动态图。
        DynamicAsyncImage(
            imageUrl = topicImageUrl,
            contentDescription = null,
            modifier = modifier,
        )
    }
}

@Preview
@Composable
// 兴趣卡片预览-未关注
private fun InterestsCardPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "Description",
                following = false,
                topicImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

@Preview
@Composable
// 兴趣卡片预览-长名字
private fun InterestsCardLongNamePreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "This is a very very very very long name",
                description = "Description",
                following = true,
                topicImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

@Preview
@Composable
// 兴趣卡片预览-长描述
private fun InterestsCardLongDescriptionPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "This is a very very very very very very very " +
                    "very very very long description",
                following = false,
                topicImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

@Preview
@Composable
// 兴趣卡片预览-空描述
private fun InterestsCardWithEmptyDescriptionPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "",
                following = true,
                topicImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}
