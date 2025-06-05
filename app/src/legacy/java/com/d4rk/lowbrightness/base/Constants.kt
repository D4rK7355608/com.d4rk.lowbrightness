package com.d4rk.lowbrightness.base

object Constants {
    const val PREF_LOW_BRIGHTNESS_ENABLED: String = "filter_enabled"
    const val PREF_DIM_LEVEL: String = "opacity_percent"
    const val PREF_OVERLAY_COLOR: String = "overlay_color"
    const val PREF_SCHEDULER_ENABLED: String = "scheduler_enabled"

    // Shared preferences file names
    const val PREF_FILE_SETTINGS: String = "settings"
    const val PREF_FILE_STARTUP: String = "startup"
    const val PREF_FILE_APP_USAGE: String = "app_usage"

    // Keys for additional preferences
    const val PREF_STARTUP_VALUE: String = "value"
    const val PREF_APP_USAGE_LAST_USED: String = "last_used"

    const val PREF_SCHEDULE_FROM_HOUR: String = "scheduleFromHour"
    const val PREF_SCHEDULE_FROM_MINUTE: String = "scheduleFromMinute"
    const val PREF_SCHEDULE_TO_HOUR: String = "scheduleToHour"
    const val PREF_SCHEDULE_TO_MINUTE: String = "scheduleToMinute"
}