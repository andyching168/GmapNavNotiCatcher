package com.andyching168.notificationcatcher

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.graphics.drawable.Icon
import android.graphics.drawable.BitmapDrawable
import java.security.MessageDigest

class NotificationCatcherService : NotificationListenerService() {
    private lateinit var viewModel: NavigationViewModel

    override fun onCreate() {
        super.onCreate()
        viewModel = NotificationCatcherApp.getInstance().getNavigationViewModel()
        // 初始化時設置為沒有通知
        viewModel.updateNavigationInfo(NavigationInfo(hasNotification = false))
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras

        if (packageName == "com.google.android.apps.maps") {
            // 記錄原始通知內容
            val rawNotification = """
                Package: $packageName
                Title: ${extras.get("android.title")}
                Direction: ${extras.get("android.text")}
                SubText: ${extras.get("android.subText")}
                Icon: ${extras.get("android.largeIcon")}
                Extras: ${extras.keySet().joinToString("\n") { key ->
                    "$key: ${extras.get(key)}"
                }}
            """.trimIndent()
            
            viewModel.setLastRawNotification(rawNotification)
            Log.d("NotificationCatcher", "原始通知內容:\n$rawNotification")

            // 從 extras 中獲取所有資訊
            val title = extras.get("android.title")?.toString() ?: ""
            val direction = extras.get("android.text")?.toString() ?: ""
            val subText = extras.get("android.subText")?.toString() ?: ""
            
            // 處理圖標資訊
            val icon = extras.get("android.largeIcon") as? Icon
            try {
                val drawable = icon?.loadDrawable(this)
                if (drawable is BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    // 獲取圖片的像素數據
                    val pixels = IntArray(bitmap.width * bitmap.height)
                    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                    
                    // 計算簡單的哈希值
                    val hash = pixels.fold(0) { acc, pixel ->
                        acc + pixel
                    }
                    
                    Log.d("NotificationCatcher", """
                        圖標信息:
                        寬度: ${bitmap.width}
                        高度: ${bitmap.height}
                        哈希值: $hash
                    """.trimIndent())
                }
            } catch (e: Exception) {
                Log.e("NotificationCatcher", "獲取圖標信息時出錯", e)
            }

            val info = parseNavigationInfo(title, direction, subText)
            Log.d("NotificationCatcher", "解析後的資訊: direction=${info.direction}")
            viewModel.updateNavigationInfo(info.copy(hasNotification = true))
            Log.d("NotificationCatcher", "更新導航資訊: $info")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        if (sbn.packageName == "com.google.android.apps.maps") {
            // 當 Google Maps 通知被移除時，設置為沒有通知
            viewModel.updateNavigationInfo(NavigationInfo(hasNotification = false))
        }
    }

    private fun parseNavigationInfo(title: String, direction: String, subText: String): NavigationInfo {
        // 解析距離和時間
        var distance = ""
        var duration = ""
        var eta = ""

        // 從 subText 中解析所有資訊
        subText.split("·").forEach { part ->
            when {
                part.contains("公尺") -> {
                    val meters = part.trim().replace("公尺", "").trim()
                    distance = if (meters.toIntOrNull() ?: 0 >= 1000) {
                        "${(meters.toIntOrNull() ?: 0) / 1000.0} 公里"
                    } else {
                        "$meters 公尺"
                    }
                }
                part.contains("公里") -> distance = part.trim()
                part.contains("分鐘") -> duration = part.trim()
                part.contains("預計到達時間") -> eta = part.trim()
            }
        }

        // 如果 subText 中沒有距離信息，則使用 title 中的距離
        if (distance.isEmpty() && title.contains("公尺")) {
            val meters = title.trim().replace("公尺", "").trim()
            distance = if (meters.toIntOrNull() ?: 0 >= 1000) {
                "${(meters.toIntOrNull() ?: 0) / 1000.0} 公里"
            } else {
                "$meters 公尺"
            }
        }

        val info = NavigationInfo(
            direction = direction,
            distance = distance,
            duration = duration,
            eta = eta,
            status = "導航中"
        )
        
        Log.d("NotificationCatcher", "解析結果: $info")
        return info
    }
} 