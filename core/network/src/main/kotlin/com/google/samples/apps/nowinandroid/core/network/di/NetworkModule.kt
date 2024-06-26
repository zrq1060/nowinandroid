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

package com.google.samples.apps.nowinandroid.core.network.di

import android.content.Context
import androidx.tracing.trace
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.util.DebugLogger
import com.google.samples.apps.nowinandroid.core.network.BuildConfig
import com.google.samples.apps.nowinandroid.core.network.demo.DemoAssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
// 单例，网络层提供：Json、FakeAssetManager、OkHttpClient、ImageLoader。
internal object NetworkModule {

    @Provides
    @Singleton
    // 单例，提供Json
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    // 单例，提供DemoAssetManager
    fun providesDemoAssetManager(
        @ApplicationContext context: Context,
        // DemoAssetManager能创建，应该是匿名内部类导致。
    ): DemoAssetManager = DemoAssetManager(context.assets::open)

    @Provides
    @Singleton
    // 单例，提供OkHttpClient
    fun okHttpCallFactory(): Call.Factory = trace("NiaOkHttpClient") {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .build()
    }

    /**
     * Since we're displaying SVGs in the app, Coil needs an ImageLoader which supports this
     * format. During Coil's initialization it will call `applicationContext.newImageLoader()` to
     * obtain an ImageLoader.
     * 因为我们要在应用中显示svg，所以Coil需要一个支持这种格式的ImageLoader。
     * 在Coil的初始化过程中，它将调用applicationContext.newImageLoader()来获取一个ImageLoader。
     *
     * @see <a href="https://github.com/coil-kt/coil/blob/main/coil-singleton/src/main/java/coil/Coil.kt">Coil</a>
     */
    @Provides
    @Singleton
    // 单例，提供ImageLoader（coil库）
    fun imageLoader(
        // We specifically request dagger.Lazy here, so that it's not instantiated from Dagger.
        okHttpCallFactory: dagger.Lazy<Call.Factory>,
        @ApplicationContext application: Context,
    ): ImageLoader = trace("NiaImageLoader") {
        ImageLoader.Builder(application)
            // 使用okHttp调用加载
            .callFactory { okHttpCallFactory.get() }
            // 支持Svg
            .components { add(SvgDecoder.Factory()) }
            // Assume most content images are versioned urls
            // but some problematic images are fetching each time
            // 假设大多数内容图像都是版本化的url，但每次都抓取一些有问题的图像。
            .respectCacheHeaders(false)
            .apply {
                if (BuildConfig.DEBUG) {
                    // debug模式下，打印日志。
                    logger(DebugLogger())
                }
            }
            .build()
    }
}
