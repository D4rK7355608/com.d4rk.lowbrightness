package com.d4rk.lowbrightness.services

import android.accessibilityservice.AccessibilityService
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.d4rk.lowbrightness.base.Constants
import com.d4rk.lowbrightness.base.Prefs
import com.d4rk.lowbrightness.ui.views.OverlayView

class AccessibilityOverlayService : AccessibilityService() {
    private var overlayView: OverlayView? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        val prefs = Prefs.get(this)
        val opacity = prefs.getInt(Constants.PREF_DIM_LEVEL, 20)
        val color = prefs.getInt(Constants.PREF_OVERLAY_COLOR, Color.BLACK)

        overlayView = OverlayView(this).apply {
            this.opacityPercentage = opacity
            this.color = color
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        }

        (getSystemService(WINDOW_SERVICE) as WindowManager).addView(overlayView, params)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        overlayView?.let {
            (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(it)
            overlayView = null
        }
        super.onDestroy()
    }

    companion object {
        fun isEnabled(context: android.content.Context): Boolean {
            val expectedComponent = android.content.ComponentName(
                context, AccessibilityOverlayService::class.java
            )
            val enabledServices = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            val colonSplitter = android.text.TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServices)
            colonSplitter.forEach {
                if (it.equals(expectedComponent.flattenToString(), ignoreCase = true)) {
                    return true
                }
            }
            return false
        }
    }
}
