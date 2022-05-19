package com.peter.feedapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import com.peter.feedapp.R
import kotlin.math.max
import kotlin.properties.Delegates

class RoundCornerImageView(context: Context, attrs: AttributeSet): AppCompatImageView(context, attrs) {
    private var viewWidth by Delegates.notNull<Float>()
    private var viewHeight by Delegates.notNull<Float>()
    private val defaultCornerRadius = 0
    private var cornerRadius by Delegates.notNull<Int>()
    private var leftTopRadius by Delegates.notNull<Int>()
    private var rightTopRadius by Delegates.notNull<Int>()
    private var rightBottomRadius by Delegates.notNull<Int>()
    private var leftBottomRadius by Delegates.notNull<Int>()

    init {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView)
        cornerRadius = ta.getDimensionPixelOffset(R.styleable.RoundCornerImageView_radius, defaultCornerRadius)
        leftTopRadius = ta.getDimensionPixelOffset(R.styleable.RoundCornerImageView_left_top_radius, defaultCornerRadius)
        rightTopRadius = ta.getDimensionPixelOffset(R.styleable.RoundCornerImageView_right_top_radius, defaultCornerRadius)
        rightBottomRadius = ta.getDimensionPixelOffset(R.styleable.RoundCornerImageView_right_bottom_radius, defaultCornerRadius)
        leftBottomRadius = ta.getDimensionPixelOffset(R.styleable.RoundCornerImageView_left_bottom_radius, defaultCornerRadius)
        // 四个值没有设对应值则使用radius的值
        if (defaultCornerRadius == leftTopRadius) {
            leftTopRadius = cornerRadius
        }

        if (defaultCornerRadius == rightTopRadius) {
            rightTopRadius = cornerRadius
        }

        if (defaultCornerRadius == rightBottomRadius) {
            rightBottomRadius = cornerRadius
        }

        if (defaultCornerRadius == leftBottomRadius) {
            leftBottomRadius = cornerRadius
        }
        ta.recycle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidth = measuredWidth.toFloat()
        viewHeight = measuredHeight.toFloat()
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        // 判断图片宽高是否大于设置圆角
        val maxLeft = max(leftTopRadius, leftBottomRadius)
        val maxRight = max(rightTopRadius, rightBottomRadius)
        val minWidth = maxLeft + maxRight
        val maxTop = max(leftTopRadius, rightTopRadius)
        val maxBottom = max(leftBottomRadius, rightBottomRadius)
        val minHeight = maxTop + maxBottom
        if (viewWidth > minWidth && viewHeight > minHeight) {
            Log.e("onLayout", "" + leftBottomRadius)
            val path = Path()
            // 左上
            path.moveTo(leftTopRadius.toFloat(), 0F)
            path.lineTo(viewWidth - rightTopRadius, 0F)
            path.quadTo(viewWidth, 0F, viewWidth, rightTopRadius.toFloat())

            // 右上
            path.lineTo(viewWidth, viewHeight - rightBottomRadius)
            path.quadTo(viewWidth, viewHeight, viewWidth - rightBottomRadius, viewHeight)

            // 右下
            path.lineTo(leftBottomRadius.toFloat(), viewHeight)
            path.quadTo(0F, viewHeight, 0F, viewHeight - leftBottomRadius)

            // 左下
            path.lineTo(0F, leftTopRadius.toFloat())
            path.quadTo(0F, 0F, leftTopRadius.toFloat(), 0F)

            canvas?.clipPath(path)
        }
        super.onDraw(canvas)
    }

}