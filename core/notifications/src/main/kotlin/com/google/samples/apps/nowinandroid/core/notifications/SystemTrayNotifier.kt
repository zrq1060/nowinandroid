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

package com.google.samples.apps.nowinandroid.core.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.InboxStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_NUM_NOTIFICATIONS = 5
private const val TARGET_ACTIVITY_NAME = "com.google.samples.apps.nowinandroid.MainActivity"
private const val NEWS_NOTIFICATION_REQUEST_CODE = 0
private const val NEWS_NOTIFICATION_SUMMARY_ID = 1
private const val NEWS_NOTIFICATION_CHANNEL_ID = ""
private const val NEWS_NOTIFICATION_GROUP = "NEWS_NOTIFICATIONS"
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.nowinandroid.apps.samples.google.com"
private const val DEEP_LINK_FOR_YOU_PATH = "foryou"
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_FOR_YOU_PATH"
const val DEEP_LINK_NEWS_RESOURCE_ID_KEY = "linkedNewsResourceId"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_BASE_PATH/{$DEEP_LINK_NEWS_RESOURCE_ID_KEY}"

/**
 * Implementation of [Notifier] that displays notifications in the system tray.
 * 在系统托盘中显示通知的[Notifier]的实现。
 */
@Singleton
// Notifier的实现-使用系统的通知。
internal class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {

    override fun postNewsNotifications(
        newsResources: List<NewsResource>,
    ) = with(context) {
        if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            // 通知权限没授予，直接返回。
            return
        }

        // 截取5个
        val truncatedNewsResources = newsResources.take(MAX_NUM_NOTIFICATIONS)

        // 新闻列表的所有通知
        val newsNotifications = truncatedNewsResources.map { newsResource ->
            // 创建新闻通知
            createNewsNotification {
                    // 图标
                    setSmallIcon(R.drawable.core_notifications_ic_nia_notification)
                    // 标题
                    .setContentTitle(newsResource.title)
                    // 内容
                    .setContentText(newsResource.content)
                    // PendingIntent
                    .setContentIntent(newsPendingIntent(newsResource))
                    // 组
                    .setGroup(NEWS_NOTIFICATION_GROUP)
                    // 单击通知时自动取消通知
                    .setAutoCancel(true)
            }
        }
        // 总结的通知，用于通知组的通知。
        val summaryNotification = createNewsNotification {
            // 标题，XXX news updates。
            val title = getString(
                R.string.core_notifications_news_notification_group_summary,
                truncatedNewsResources.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(R.drawable.core_notifications_ic_nia_notification)
                // Build summary info into InboxStyle template.
                // 在InboxStyle模板中构建摘要信息。
                // 设置此通知的样式，标题（XXX news updates）+子内容（一行标题）列表。
                .setStyle(newsNotificationStyle(truncatedNewsResources, title))
                .setGroup(NEWS_NOTIFICATION_GROUP)
                // !!!将此通知设置为通知组的组摘要
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // Send the notifications
        // 发送通知
        val notificationManager = NotificationManagerCompat.from(this)
        // -发送新闻的所有通知
        newsNotifications.forEachIndexed { index, notification ->
            notificationManager.notify(
                truncatedNewsResources[index].id.hashCode(),
                notification,
            )
        }
        // -发送总结的通知
        // 说明：两个通知类型（newsNotifications、summaryNotification），通过相同的组Group进行关联，一个为组摘要，一个为组内容。
        notificationManager.notify(NEWS_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    /**
     * Creates an inbox style summary notification for news updates
     * 为新闻更新创建收件箱样式的摘要通知
     */
    private fun newsNotificationStyle(
        newsResources: List<NewsResource>,
        title: String,
    ): InboxStyle = newsResources
        // fold，初始化为InboxStyle，每个新闻增加一行，内容为标题。
        .fold(InboxStyle()) { inboxStyle, newsResource -> inboxStyle.addLine(newsResource.title) }
        // 大内容标题
        .setBigContentTitle(title)
        // 摘要文本
        .setSummaryText(title)
}

/**
 * Creates a notification for configured for news updates
 * 创建为新闻更新配置的通知
 */
private fun Context.createNewsNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    // 确保通知通道（名为：News updates）存在
    ensureNotificationChannelExists()
    // 创建
    return NotificationCompat.Builder(
        this,
        NEWS_NOTIFICATION_CHANNEL_ID,
    )
        // 优先级
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // 供外部修改使用
        .apply(block)
        // 构建
        .build()
}

/**
 * Ensures that a notification channel is present if applicable
 * 如果适用，确保存在通知通道
 */
private fun Context.ensureNotificationChannelExists() {
    // android 8.0以前直接返回
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        NEWS_NOTIFICATION_CHANNEL_ID,
        // title
        getString(R.string.core_notifications_news_notification_channel_name),
        // 重要性
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        // 描述
        description = getString(R.string.core_notifications_news_notification_channel_description)
    }
    // Register the channel with the system
    // 向系统注册通道
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.newsPendingIntent(
    newsResource: NewsResource,
): PendingIntent? = PendingIntent.getActivity(
    // Context
    this,
    // requestCode
    NEWS_NOTIFICATION_REQUEST_CODE,
    // Intent
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = newsResource.newsDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    // flags
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

private fun NewsResource.newsDeepLinkUri() = "$DEEP_LINK_BASE_PATH/$id".toUri()
