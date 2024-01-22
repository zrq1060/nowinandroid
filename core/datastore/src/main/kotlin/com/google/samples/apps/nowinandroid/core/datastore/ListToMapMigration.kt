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

package com.google.samples.apps.nowinandroid.core.datastore

import androidx.datastore.core.DataMigration

/**
 * Migrates from using lists to maps for user data.
 * 将用户数据从使用list迁移到使用maps。
 */
// List转Map的迁移
internal object ListToMapMigration : DataMigration<UserPreferences> {

    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // Migrate topic id lists
            followedTopicIds.clear()
            followedTopicIds.putAll(
                // list转map，value为true。
                currentData.deprecatedFollowedTopicIdsList.associateWith { true },
            )
            deprecatedFollowedTopicIds.clear()

            // Migrate author ids
            followedAuthorIds.clear()
            followedAuthorIds.putAll(
                // list转map，value为true。
                currentData.deprecatedFollowedAuthorIdsList.associateWith { true },
            )
            deprecatedFollowedAuthorIds.clear()

            // Migrate bookmarks
            bookmarkedNewsResourceIds.clear()
            bookmarkedNewsResourceIds.putAll(
                // list转map，value为true。
                currentData.deprecatedBookmarkedNewsResourceIdsList.associateWith { true },
            )
            deprecatedBookmarkedNewsResourceIds.clear()

            // Mark migration as complete
            // 将迁移标记为完成
            hasDoneListToMapMigration = true
        }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneListToMapMigration
}
