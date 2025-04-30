package com.andyching168.notificationcatcher

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel : ViewModel() {
    private val _navigationInfo = MutableStateFlow(NavigationInfo())
    val navigationInfo: StateFlow<NavigationInfo> = _navigationInfo.asStateFlow()

    private var lastRawNotification: String = ""
    private var lastIconHash: String = ""

    // 哈希值對應表
    private val iconHashMap: Map<String, String> = mapOf(
        "0-0-59-3-71-99-151-123-95-0-71-11-47-0-0-0" to "right",  // 右轉
        "3-59-0-0-123-151-99-71-11-71-0-95-0-0-0-47" to "left",   // 左轉
        "0-39-39-0-0-175-175-0-0-55-55-0-0-23-23-0" to "straight", // 直行
        "0-39-39-0-0-175-175-0-0-63-63-0-0-31-31-0" to "straight", // 直行
        "0-47-119-0-0-79-191-0-23-111-0-0-31-31-0-0" to "side_right" // 靠右
    )

    fun updateNavigationInfo(info: NavigationInfo) {
        _navigationInfo.value = info
    }

    fun setLastRawNotification(raw: String) {
        lastRawNotification = raw
    }

    fun setLastIconHash(hash: String) {
        lastIconHash = hash
        val direction = iconHashMap[hash]
        if (direction != null) {
            Log.d("NotificationCatcher", "圖標哈希值: $hash 對應方向: $direction")
        } else {
            Log.d("NotificationCatcher", "未知的圖標哈希值: $hash")
        }
    }

    fun getLastTurnDirection(): String {
        return iconHashMap[lastIconHash] ?: ""
    }

    fun showRawNotification(context: Context) {
        if (lastRawNotification.isNotEmpty()) {
            Log.d("NotificationCatcher", "顯示原始通知內容:\n$lastRawNotification")
            Toast.makeText(context, lastRawNotification, Toast.LENGTH_LONG).show()
        } else {
            Log.d("NotificationCatcher", "目前沒有通知內容")
            Toast.makeText(context, "目前沒有通知內容", Toast.LENGTH_SHORT).show()
        }
    }
} 