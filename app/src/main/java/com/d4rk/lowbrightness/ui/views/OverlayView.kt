package com.d4rk.lowbrightness.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.appcompat.widget.AppCompatImageView

class OverlayView(context: Context) : AppCompatImageView(context) {

    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        textSize = 10f
    }

    var opacityPercentage: Int = 0
        set(value) {
            field = value
            paint.alpha = 255 * value / 100
            invalidate()
        }

    var color: Int = 0
        set(value) {
            field = value
            paint.color = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(paint)
    }
}