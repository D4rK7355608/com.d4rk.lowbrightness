package com.d4rk.lowbrightness.app.brightness.domain.receivers

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.d4rk.android.libs.apptoolkit.app.settings.general.ui.GeneralSettingsActivity
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.ui.components.closeDialog
import com.d4rk.lowbrightness.app.brightness.ui.components.closeNightScreen
import com.d4rk.lowbrightness.app.brightness.ui.components.showDialogAndNightScreen
import com.d4rk.lowbrightness.app.settings.settings.utils.constants.SettingsConstants
import com.d4rk.lowbrightness.appContext

class NightScreenReceiver : BroadcastReceiver() {
    companion object {
        const val CLOSE_NOTIFICATION_ACTION = "com.d4rk.lowbrightness.CLOSE_NOTIFICATION"
        const val SHOW_NOTIFICATION_ACTION = "com.d4rk.lowbrightness.SHOW_NOTIFICATION"
        const val CLOSE_DIALOG_ACTION = "com.d4rk.lowbrightness.CLOSE_DIALOG"
        const val SHOW_DIALOG_AND_NIGHT_SCREEN_ACTION =
            "com.d4rk.lowbrightness.SHOW_DIALOG_AND_NIGHT_SCREEN"
        const val POWER_OFF_ACTION = "com.d4rk.lowbrightness.POWER_OFF"
        const val CHANNEL_ID = "NightScreenNotification"
        const val NOTIFICATION_ID = 1
        private const val EXTRA_TITLE_GS: String = "extra_title"
        private const val EXTRA_CONTENT_GS: String = "extra_content"

        fun sendBroadcast(
            context: Context = appContext ,
            action: String = SHOW_NOTIFICATION_ACTION
        ) {
            val intent = Intent(context , NightScreenReceiver::class.java)
            intent.`package` = context.packageName
            intent.action = action
            intent.component = ComponentName(context.packageName , "com.d4rk.lowbrightness.ui.NightScreenReceiver")
            context.sendBroadcast(intent)
        }
    }

    @SuppressLint("LaunchActivityFromNotification")
    override fun onReceive(context: Context? , intent: Intent?) {
        context ?: return
        val action = intent?.action ?: return
        when (action) {
            CLOSE_NOTIFICATION_ACTION -> {
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)
                    ?.cancel(NOTIFICATION_ID)
            }

            SHOW_NOTIFICATION_ACTION -> {
                val clickIntent = Intent(context , NightScreenReceiver::class.java).apply {
                    this.`package` = context.packageName
                    this.action = SHOW_DIALOG_AND_NIGHT_SCREEN_ACTION
                }
                val clickPendingIntent = PendingIntent.getBroadcast(
                    context , 0 , clickIntent , PendingIntent.FLAG_IMMUTABLE
                )

                val closeDialogIntent = Intent(context , NightScreenReceiver::class.java).apply {
                    this.action = POWER_OFF_ACTION
                }
                val closePendingIntent = PendingIntent.getBroadcast(
                    context , 0 , closeDialogIntent , PendingIntent.FLAG_IMMUTABLE
                )
                val settingDialogIntent = Intent(context, GeneralSettingsActivity::class.java).apply {
                    putExtra(EXTRA_TITLE_GS, context.getString(R.string.settings_brightness_title))
                    putExtra(EXTRA_CONTENT_GS, SettingsConstants.KEY_SETTINGS_BRIGHTNESS)
                    // The library's start method adds FLAG_ACTIVITY_NEW_TASK.
                    // This is generally needed if starting an activity from a context that is not an activity (like a BroadcastReceiver).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                val settingPendingIntent = PendingIntent.getActivity(
                    context , 0 , settingDialogIntent , PendingIntent.FLAG_IMMUTABLE
                )
                val builder = NotificationCompat.Builder(context , CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.night_screen_is_running))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(clickPendingIntent)
                    .addAction(
                        R.drawable.ic_power_settings_new_24 ,
                        context.getString(R.string.power_off) ,
                        closePendingIntent
                    )
                    .addAction(
                        R.drawable.ic_settings_24 ,
                        context.getString(com.d4rk.android.libs.apptoolkit.R.string.settings) ,
                        settingPendingIntent
                    )

                createNotificationChannel()

                with(NotificationManagerCompat.from(context)) {
                    // notificationId is a unique int for each notification that you must define
                    if (ActivityCompat.checkSelfPermission(
                            context , Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        notify(NOTIFICATION_ID, builder.build())
                    }
                }
            }

            CLOSE_DIALOG_ACTION -> {
                closeDialog()
            }

            SHOW_DIALOG_AND_NIGHT_SCREEN_ACTION -> {
                showDialogAndNightScreen()
            }

            POWER_OFF_ACTION -> {
                closeNightScreen()
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID
            val descriptionText = appContext.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID , name , importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}