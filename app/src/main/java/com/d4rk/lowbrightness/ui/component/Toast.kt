package com.d4rk.lowbrightness.ui.component

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.d4rk.lowbrightness.appContext

private var uiThreadHandler: Handler = Handler(Looper.getMainLooper())

fun CharSequence.showToast(duration: Int = Toast.LENGTH_SHORT) {
    uiThreadHandler.post {
        val toast = Toast.makeText(appContext, this, duration)
        toast.duration = duration
        toast.show()
    }
}
