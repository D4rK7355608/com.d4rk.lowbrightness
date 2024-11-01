package com.d4rk.lowbrightness.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.d4rk.lowbrightness.MainActivity
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.base.Application
import com.d4rk.lowbrightness.base.Constants
import com.d4rk.lowbrightness.base.Prefs
import com.d4rk.lowbrightness.ui.views.OverlayView

class OverlayService : Service() {
    private var mView : OverlayView? = null

    override fun onBind(intent : Intent) : IBinder? = null

    override fun onStartCommand(intent : Intent , flags : Int , startId : Int) : Int {
        val sharedPreferences = Prefs.get(baseContext)

        if (! isEnabled(baseContext) || ! Application.canDrawOverlay(baseContext)) {
            stopSelf()
            return START_NOT_STICKY
        }

        val opacityPercentage = sharedPreferences.getInt(Constants.PREF_DIM_LEVEL , 20)
        val color = sharedPreferences.getInt(Constants.PREF_OVERLAY_COLOR , Color.BLACK)

        if (mView == null) {
            mView = OverlayView(this).apply {
                setOpacityPercentage(opacityPercentage)
                setColor(color)
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT ,
                WindowManager.LayoutParams.MATCH_PARENT ,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY ,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS ,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                horizontalMargin = 0f
                verticalMargin = 0f
            }

            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            wm.addView(mView , params)
        }
        else {
            mView?.apply {
                setOpacityPercentage(opacityPercentage)
                setColor(color)
                redraw()
            }
        }

        showNotification()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID , getString(R.string.screen_overlay_notifications) , importance
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    private fun showNotification() {
        createNotificationChannel()

        val mBuilder = NotificationCompat.Builder(this , NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_eye)
                .setContentTitle(getString(R.string.notification_color_light_filter_active_title))
                .setContentText(getString(R.string.notification_context)).setOngoing(true)

        val resultIntent = Intent(this , MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this).apply {
            addParentStack(MainActivity::class.java)
            addNextIntent(resultIntent)
        }

        val resultPendingIntent = stackBuilder.getPendingIntent(
            0 , PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mBuilder.setContentIntent(resultPendingIntent)
        startForeground(1000 , mBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        mView?.let {
            (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(it)
            mView = null
        }
        stopForeground(true)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "overlay_service"

        @JvmStatic
        fun isEnabled(context : Context) : Boolean {
            val prefs = Prefs.get(context)
            return prefs.getBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED , false)
        }
    }
}
