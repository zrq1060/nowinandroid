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

package com.google.samples.apps.nowinandroid.core.database

import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

/**
 * Automatic schema migrations sometimes require extra instructions to perform the migration, for
 * example, when a column is renamed. These extra instructions are placed here by creating a class
 * using the following naming convention `SchemaXtoY` where X is the schema version you're migrating
 * from and Y is the schema version you're migrating to. The class should implement
 * `AutoMigrationSpec`.
 * 自动模式迁移有时需要额外的指令来执行迁移，例如，在重命名列时。
 * 这些额外的指令是通过使用以下命名约定SchemaXtoY创建一个类放在这里的，其中X是要迁移的模式版本，Y是要迁移到的模式版本。
 * 该类应该实现AutoMigrationSpec。
 */
// 数据库的迁移操作
internal object DatabaseMigrations {

    @RenameColumn(
        tableName = "topics",
        fromColumnName = "description",
        toColumnName = "shortDescription",
    )
    // 版本2->3，重命名topics表的description为shortDescription。
    class Schema2to3 : AutoMigrationSpec

    @DeleteColumn(
        tableName = "news_resources",
        columnName = "episode_id",
    )
    @DeleteTable.Entries(
        DeleteTable(
            tableName = "episodes_authors",
        ),
        DeleteTable(
            tableName = "episodes",
        ),
    )
    // 版本10->11，删除news_resources表的episode_id、删除episodes_authors表和episodes表。
    class Schema10to11 : AutoMigrationSpec

    @DeleteTable.Entries(
        DeleteTable(
            tableName = "news_resources_authors",
        ),
        DeleteTable(
            tableName = "authors",
        ),
    )
    // 版本11->12，删除news_resources_authors表和authors表。
    class Schema11to12 : AutoMigrationSpec
}
