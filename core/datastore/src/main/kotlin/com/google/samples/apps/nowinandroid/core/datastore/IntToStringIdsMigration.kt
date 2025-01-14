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
 * Migrates saved ids from [Int] to [String] types
 * 将保存的id从[Int]类型迁移到[String]类型
 */
// Int转String的迁移
internal object IntToStringIdsMigration : DataMigration<UserPreferences> {

    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // Migrate topic ids
            // 迁移主题ids
            deprecatedFollowedTopicIds.clear()
            deprecatedFollowedTopicIds.addAll(
                currentData.deprecatedIntFollowedTopicIdsList.map(Int::toString),
            )
            deprecatedIntFollowedTopicIds.clear()

            // Migrate author ids
            // 迁移作者ids
            deprecatedFollowedAuthorIds.clear()
            deprecatedFollowedAuthorIds.addAll(
                currentData.deprecatedIntFollowedAuthorIdsList.map(Int::toString),
            )
            deprecatedIntFollowedAuthorIds.clear()

            // Mark migration as complete
            // 将迁移标记为完成
            hasDoneIntToStringIdMigration = true
        }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneIntToStringIdMigration
}
