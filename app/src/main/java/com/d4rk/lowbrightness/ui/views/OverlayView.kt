package com.d4rk.lowbrightness.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.appcompat.widget.AppCompatImageView

class OverlayView(context : Context) : AppCompatImageView(context) {

    private val loadPaint : Paint = Paint().apply {
        isAntiAlias = true
        textSize = 10f
        color = color
        alpha = 255 / 100 * opacityPercentage
    }

    private var opacityPercentage : Int = 0

    private var color : Int = 0

    override fun onDraw(canvas : Canvas) {
        super.onDraw(canvas)
        canvas.drawPaint(loadPaint)
    }

    fun redraw() {
        invalidate()
    }

    fun setOpacityPercentage(opacityPercentage : Int) {
        loadPaint.alpha = 255 / 100 * opacityPercentage
        this.opacityPercentage = opacityPercentage
    }

    fun setColor(color : Int) {
        loadPaint.color = color
        setOpacityPercentage(opacityPercentage)
        this.color = color
    }

    fun getColor() : Int {
        return color
    }
}