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

    fun updateNavigationInfo(info: NavigationInfo) {
        _navigationInfo.value = info
    }

    fun setLastRawNotification(raw: String) {
        lastRawNotification = raw
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