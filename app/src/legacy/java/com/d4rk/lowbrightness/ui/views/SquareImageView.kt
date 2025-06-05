package com.d4rk.lowbrightness.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max

class SquareImageView : AppCompatImageView {
    constructor(context : Context) : super(context)
    constructor(context : Context , attrs : AttributeSet?) : super(context , attrs)
    constructor(context : Context , attrs : AttributeSet? , defStyleAttr : Int) : super(
        context , attrs , defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec : Int , heightMeasureSpec : Int) {
        super.onMeasure(widthMeasureSpec , heightMeasureSpec)
        val size = max(measuredWidth.toDouble() , measuredHeight.toDouble()).toInt()
        setMeasuredDimension(size , size)
    }
}