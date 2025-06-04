package com.d4rk.lowbrightness.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun Context.openUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
}

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("text", text)
    clipboard.setPrimaryClip(clip)
}

