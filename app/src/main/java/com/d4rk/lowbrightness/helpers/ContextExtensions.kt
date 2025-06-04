package com.d4rk.lowbrightness.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.content.ClipboardManager
import android.content.ClipData

fun Context.openUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("text", text)
    clipboard.setPrimaryClip(clip)
}

