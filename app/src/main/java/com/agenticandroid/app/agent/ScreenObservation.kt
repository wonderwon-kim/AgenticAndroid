package com.agenticandroid.app.agent

import android.graphics.Bitmap

data class ScreenObservation(
    val screenshot: Bitmap? = null,
    val width: Int = 0,
    val height: Int = 0,
    val orientation: String = "portrait",
    val currentApp: String = "unknown",
    val previousAction: Action? = null,
    val taskProgress: String = "",
    val actionHistorySummary: String = "",
    val screenChanged: Boolean = false
)
