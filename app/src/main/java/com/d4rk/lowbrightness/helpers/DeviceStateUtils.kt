package com.d4rk.lowbrightness.helpers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs

fun Context.hasSufficientStorage(minBytes: Long): Boolean {
    val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        filesDir
    } else {
        Environment.getDataDirectory()
    }
    val stat = StatFs(path.path)
    val availableBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        stat.availableBytes
    } else {
        stat.blockSizeLong * stat.availableBlocksLong
    }
    return availableBytes >= minBytes
}

fun Context.isBatteryLevelAcceptable(threshold: Int = 20): Boolean {
    val manager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val level = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    return level >= threshold
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
