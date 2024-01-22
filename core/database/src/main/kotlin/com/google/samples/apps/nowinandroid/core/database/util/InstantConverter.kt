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

package com.google.samples.apps.nowinandroid.core.database.util

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

// 时间转换
internal class InstantConverter {
    @TypeConverter
    // 数据库的类型转换，把Long（毫秒）类型转为Instant。
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    // 数据库的类型转换，把Instant类型转为Long（毫秒）。
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}
