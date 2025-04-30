package com.andyching168.notificationcatcher

data class NavigationInfo(
    val direction: String = "",
    val distance: String = "",
    val duration: String = "",
    val eta: String = "",
    val status: String = "",
    val isRerouting: Boolean = false,
    val hasNotification: Boolean = false,
    val iconResId: Int = 0
) 